//@@author zenghou
package logic;

import java.text.DecimalFormat;
import java.util.logging.Logger;

import com.ib.client.EClientSocket;
import com.ib.client.Order;

import model.ContractWithPriceDetail;
import model.Model;

public class LogicManager implements Logic {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Model model;
    private EClientSocket eClientSocket;
    private EWrapperImplementation eWrapperImplementation;

    private int requestId = 1;

    public LogicManager(Model modelManager, EClientSocket eClientSocket, EWrapperImplementation eWrapperImplementation) {
        this.model = modelManager;
        this.eClientSocket = eClientSocket;
        this.eWrapperImplementation = eWrapperImplementation;

        Parser parser = new Parser("/Users/ZengHou/Desktop/tickersWithPrice.csv", model);
        parser.readDataUpdateModel();

        // called after listOfSymbol is populated by parser#readDataUpdateModel()
        model.initializeModel();
        model.getUniqueOrderContractList().setLogic(this);
    }

    /**
     * Loops through model's contract list and retrieves realtimebars for each stock inside
     * eClientSocket to transmit request message from client to TWS server
     * @throws InterruptedException
     */
    @Override
    public void getRealTimeBars() throws InterruptedException {
        LOGGER.info("=============================[ Requesting for real time bars ]===========================");

        for (ContractWithPriceDetail contract: model.getViewOnlyContractWithPriceDetailList()) {
            // Print log
            // System.out.println("Getting price for: " + contract.symbol());
            // System.out.println("current id is: " + requestId);

            LOGGER.info("=============================[ Req " + requestId + ": Retrieving price for " +
                    contract.symbol() + " ]=============================");

            // set unique req Id for each contract
            setRequestIdForContractWithPriceDetail(requestId, contract);

            eClientSocket.reqRealTimeBars(requestId, contract, 5, "MIDPOINT",
                    true, null);
            requestId++;
        }
    }

    @Override
    public void cancelRealTimeBars() {
        for (int i = 1; i < requestId; i++) {
            eClientSocket.cancelRealTimeBars(i);
        }
    }

    @Override
    public void cancelRealTimeBarsForContract(ContractWithPriceDetail contract) {
        int contractRequestId = contract.getRequestId();
        eClientSocket.cancelRealTimeBars(contractRequestId);
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

        System.out.println("Current id: " + currentOrderId + " next valid is: " + eWrapperImplementation.getCurrentOrderId());
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
}
