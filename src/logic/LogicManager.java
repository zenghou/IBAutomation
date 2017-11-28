package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.Order;

import model.ContractWithPriceDetail;
import model.Model;

public class LogicManager implements Logic {
    private Model model;
    private Parser parser;
    private EClientSocket eClientSocket;
    private int requestId = 1;
    private int orderId = 1;

    public LogicManager(Model modelManager, EClientSocket eClientSocket) {
        this.model = modelManager;
        this.eClientSocket = eClientSocket;
        parser = new Parser("/Users/ZengHou/Desktop/testStockList.csv", model);
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
        for (ContractWithPriceDetail contract: model.getViewOnlyContractWithPriceDetailList()) {
            // Print log
            System.out.println("Getting price for: " + contract.symbol());
            System.out.println("current id is: " + requestId);

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

    /**
     * Private method to be called by {@code getRealTimeBars}
     * Although setting of requestId for a ContractWithPriceDetail is fairly simple, it is extracted in to abide by
     * single responsibility principle. Moreover, this method handles the exception, which prevents long try catch
     * block in getRealTimeBars.
     */
    private void setRequestIdForContractWithPriceDetail(int reqId, ContractWithPriceDetail contract) {
        try {
            contract.setRequestId(reqId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void placeLimitBuyOrder(ContractWithPriceDetail contractWithPriceDetail) {
        double limitPrice = contractWithPriceDetail.getCurrentPrice();

        assert(limitPrice > 0);

        int quantityToBePurchased = calculateSharesBuyableWithSumAtPrice(300.00, limitPrice);

        Order orderToBeSubmitted = createLimitBuyOrder(quantityToBePurchased, limitPrice);

        eClientSocket.placeOrder(orderId, contractWithPriceDetail, orderToBeSubmitted);
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

    @Override
    public boolean hasFallenByPercentage(Contract contract, int percentage) {
        return false;
    }

    @Override
    public double getOpeningPrice(String stockSymbol) {
        return 0;
    }
}
