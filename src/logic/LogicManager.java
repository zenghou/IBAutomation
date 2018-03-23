//@@author zenghou
package logic;

import java.text.DecimalFormat;
import java.util.Observable;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;
import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Order;

import Events.EventManager;
import Events.EventsCenter;
import model.ContractWithPriceDetail;
import model.ListOfUniqueContractList;
import model.Model;
import model.UniqueContractList;

public class LogicManager extends EventManager implements Logic{
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Model model;
    private EClientSocket eClientSocket;
    private EWrapperImplementation eWrapperImplementation;
    private Parser parser;

    private ScheduledCancelUnfilledOrders cancelUnfilledOrdersTask;
    private ScheduledMarketOnClose marketOnCloseTask;

    private int requestId = 1;
    private int numberOfSellLimitOrdersSubmitted = 0;

    public LogicManager(Model modelManager, EClientSocket eClientSocket, EWrapperImplementation eWrapperImplementation) {
        this.model = modelManager;
        this.eClientSocket = eClientSocket;
        this.eWrapperImplementation = eWrapperImplementation;

        parser = new Parser("/Users/ZengHou/Desktop/tickersWithPrice.csv", model);
        parser.readDataUpdateModel();

        // called after listOfSymbol is populated by parser#readDataUpdateModel()
        model.initializeModel();
        model.getUniqueOrderContractList().setLogic(this);

        cancelUnfilledOrdersTask = new ScheduledCancelUnfilledOrders(this, model);
        marketOnCloseTask = new ScheduledMarketOnClose(this, model);
        registerAsAnEventHandler(this);
    }

    /**
     * Registers the object as an event handler at the {@link EventsCenter}
     * @param handler usually {@code this}
     */
    protected void registerAsAnEventHandler(Object handler) {
        EventsCenter.getInstance().registerHandler(handler);
    }

    @Override
    public Parser getParser() {
        return parser;
    }

    // ===================================================================================================
    // ===================================== HANDLES BUYING ASPECT =======================================
    // ===================================================================================================

    /**
     * Loops through model's contract list and retrieves real time market data for each stock inside
     * eClientSocket to transmit request message from client to TWS server
     * @throws InterruptedException
     */
    @Override
    public void getRealTimeMarketData() {
        ListOfUniqueContractList uniqueContractLists = model.getUniqueContractLists();

        UniqueContractList contractList = uniqueContractLists.getNextUniqueContractList();

        for (ContractWithPriceDetail contract: contractList.getInternalArray()) {
            setRequestIdForContractWithPriceDetail(requestId, contract);

            eClientSocket.reqMktData(requestId, contract, "", false,
                    false, null);
            requestId++;
        }
    }

    public void getRealTimeMarketDataForContract(ContractWithPriceDetail contract) {
        setRequestIdForContractWithPriceDetail(requestId, contract);
        eClientSocket.reqMktData(requestId, contract, "", false,
                false, null);
        requestId++;
    }

    @Override
    public int getCurrentOrderId() {
        return eWrapperImplementation.getCurrentOrderId();
    }

    @Override
    public void incrementOrderId() {
        eWrapperImplementation.incrementOrderId();
    }

    @Override
    public void cancelRealTimeBarsForContract(ContractWithPriceDetail contract) {
        int contractRequestId = contract.getRequestId();
        eClientSocket.cancelRealTimeBars(contractRequestId);

        LOGGER.info("=============================[ Cancelling realTimeBars for ID:  " + contractRequestId + ", Symbol: " +
                contract.symbol() + " ]=============================");
    }

    @Override
    public void cancelRealTImeMarketDataForContract(ContractWithPriceDetail contract) {
        int contractRequestId = contract.getRequestId();
        eClientSocket.cancelMktData(contractRequestId);

        LOGGER.info("=============================[ Cancelling reqMktData for ID:  " + contractRequestId + ", Symbol: " +
                contract.symbol() + " ]=============================");
    }

    /**
     * Private method to be called by {@code getRealTimeBars}
     * Although setting of requestId for a ContractWithPriceDetail is fairly simple, it is extracted in to abide by
     * single responsibility principle. Moreover, this method handles the exception, which prevents long try catch
     * block in getRealTimeBars.
     */
    private void setRequestIdForContractWithPriceDetail(int reqId, ContractWithPriceDetail contract) {
        try {
            LOGGER.info("=============================[ Assigning reqId " + reqId + " to " + contract.symbol() +
                    " ]===========================");

            contract.setRequestId(reqId);
        } catch (Exception e) {
            LOGGER.severe("=============================[ Unable to assign reqId to " + contract.symbol() +
                    " ]===========================");

            e.printStackTrace();
        }
    }

    @Override
    public void placeLimitBuyOrder(ContractWithPriceDetail contractWithPriceDetail, double percentageBelow, double sum) {
        // e.g. (100 - 16) = 84% would mean 0.84
        double percentage = (100.00 - percentageBelow)/100.00;

        double unformattedLimitPrice = contractWithPriceDetail.getDayOpeningPrice() * percentage;

        double limitPrice = formatOrderPrice(unformattedLimitPrice);

        assert(limitPrice > 0);

        int quantityToBePurchased = calculateSharesBuyableWithSumAtPrice(sum, limitPrice);

        Order orderToBeSubmitted = createLimitBuyOrder(quantityToBePurchased, limitPrice);

        LOGGER.severe("=============================[ Attempting to place order for " + quantityToBePurchased + " of " +
                contractWithPriceDetail.symbol() + " at " + limitPrice + " ]===========================");

        int currentOrderId = eWrapperImplementation.getCurrentOrderId();

        eClientSocket.placeOrder(currentOrderId, contractWithPriceDetail, orderToBeSubmitted);

        eWrapperImplementation.incrementOrderId();

        System.out.println("Placed order with Id: " + currentOrderId + ". Next valid Id is: " + eWrapperImplementation.
                getCurrentOrderId());
    }

    /**
     * Formats the order price of a stock. If price is $1 and below, round down to nearest 4 decimal places. Otherwise,
     * price will be rounded off to the nearest 2 decimal places.
     */
    private double formatOrderPrice(double price) {
        DecimalFormat decimalFormat;

        if (price <= 1.00) {
            decimalFormat = new DecimalFormat("0.0000");
        } else {
            decimalFormat = new DecimalFormat("0.00");
        }

        String formatted = decimalFormat.format(price);

        return Double.valueOf(formatted);
    }

    /** Creates a buy order of {@code quantity} at {@code limitPrice} */
    private Order createLimitBuyOrder(int quantity, double limitPrice) {
        Order order = new Order();
        order.action("BUY");
        order.orderType("LMT");
        order.totalQuantity(quantity);
        order.lmtPrice(limitPrice);
        return order;
    }

    /** Calculates the number of shares that can be purchased with {@code sum} at {@code purchasePrice}*/
    private int calculateSharesBuyableWithSumAtPrice(double sum, double purchasePrice) {
        return (int) Math.floor(sum/purchasePrice);
    }

    // =======================================================================================
    // ============================= HANDLES SELLING ASPECT ==================================
    // =======================================================================================

    @Override
    public void requestAccountUpdates() {
        eClientSocket.reqAccountUpdates(true, "U9557107");

        LOGGER.info("=============================[ Requesting for Account Updates ]===========================");
    }

    @Override
    public void cancelAccountUpdates() {
        eClientSocket.reqAccountUpdates(false, "U9557107");

        LOGGER.info("=============================[ Cancelling subscription for Account Updates ]=================" +
                "==========");
    }

    @Override
    public void setLimitSellOrdersForAllExistingPositions(double percentageAboveOrderPrice) {
        for (ContractWithPriceDetail contractWithPriceDetail: model.getUniqueContractToCloseList().
                getContractArrayWithPriceDetailList()) {

            int quantityToBeSold = (int) contractWithPriceDetail.getPosition();
            double priceToBeSoldAt = getPriceAboveOrderPriceBySomePercent(percentageAboveOrderPrice, contractWithPriceDetail);

            Order limitSellOrder = createLimitSellOrder(quantityToBeSold, priceToBeSoldAt);

            LOGGER.severe("***************************[ Attempting to place sell order for " + quantityToBeSold + " of " +
                    contractWithPriceDetail.symbol() + " at " + percentageAboveOrderPrice + "% above order price at: " +
                    priceToBeSoldAt + " ]***************************");

            int currentOrderId = eWrapperImplementation.getCurrentOrderId();

            eClientSocket.placeOrder(currentOrderId, contractWithPriceDetail, limitSellOrder);

            LOGGER.info("=============================[ Attempting to add orderId: " + currentOrderId + " to Model" +
                    " ]=============================");

            model.addSellLimitOrderId(currentOrderId);

            eWrapperImplementation.incrementOrderId();

            System.out.println("Current id: " + currentOrderId + " next valid is: " + eWrapperImplementation.getCurrentOrderId());

            // increment count for positions closed
            numberOfSellLimitOrdersSubmitted++;
        }

        LOGGER.severe("=============================[ Attempted to submit " + numberOfSellLimitOrdersSubmitted +
                "sell limit position(s) ]=============================" );
    }

    /** Creates a market sell order of {@code quantity} at {@code limitPrice} */
    private Order createLimitSellOrder(int quantity, double limitPrice) {
        Order order = new Order();
        order.action("SELL");
        order.orderType("LMT");
        order.totalQuantity(quantity);
        order.lmtPrice(limitPrice);
        return order;
    }

    /** Gets the a price that is {@code percentage} above the order price for a {@code contractWithPriceDetail} */
    private double getPriceAboveOrderPriceBySomePercent (double percentage, ContractWithPriceDetail contractWithPriceDetail) {
        double percentageAboveAveragePrice = contractWithPriceDetail.getAverageCost() * ((100 + percentage)/100);

        return formatOrderPrice(percentageAboveAveragePrice);
    }

    @Override
    public void cancelOrder(int orderId) {
        LOGGER.info("=============================[ Submitting cancellation for Sell Limit order id: " + orderId +
                " ]=============================");

        eClientSocket.cancelOrder(orderId);
    }

    @Override
    public void placeOrder(int currentOrderId, Contract contract, Order order) {
        eClientSocket.placeOrder(currentOrderId, contract, order);
    }

    @Override
    public ScheduledMarketOnClose getScheduledMarketOnCloseTask() {
        return marketOnCloseTask;
    }

    @Override
    public ScheduledCancelUnfilledOrders getScheduledCancelUnfilledOrdersTask() {
        return cancelUnfilledOrdersTask;
    }

    // ===================================================================================================
    // ============= HANDLES UPDATING WHEN NEW CONTRACT IS ADDED TO UNIQUE CONTRACT LIST =================
    // ===================================================================================================

    @Override
    public void update(Observable o, Object arg) {
        ContractWithPriceDetail contract = (ContractWithPriceDetail) arg;
        LOGGER.info("=============================[ New contract [" + contract.symbol() +
                "] has been added to model. ]=============================");
        getRealTimeMarketDataForContract(contract);
    }
}
