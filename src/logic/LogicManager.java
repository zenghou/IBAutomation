package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import model.ContractWithPriceDetail;
import model.Model;

public class LogicManager implements Logic {
    private Model model;
    private Parser parser;
    private int requestId = 1;

    public LogicManager(Model modelManager) {
        model = modelManager;
        parser = new Parser("/Users/ZengHou/Desktop/testStockList.csv", model);
        parser.readDataUpdateModel();

        // called after listOfSymbol is populated by parser#readDataUpdateModel()
        model.initializeModel();
    }

    /**
     * Loops through model's contract list and retrieves realtimebars for each stock inside
     * @param eClientSocket to transmit request message from client to TWS server
     * @throws InterruptedException
     */
    @Override
    public void getRealTimeBars(EClientSocket eClientSocket) throws InterruptedException {
        for (ContractWithPriceDetail contract: model.getViewOnlyContractWithPriceDetailList()) {
            // Print log
            System.out.println("Getting price for: " + contract.symbol());
            System.out.println("current id is: " + requestId);

            setRequestIdForContractWithPriceDetail(requestId, contract);

            eClientSocket.reqRealTimeBars(requestId, contract, 5, "MIDPOINT",
                    true, null);
            requestId++;
        }
    }

    public void cancelRealTimeBars(EClientSocket eClientSocket) {
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
    public void placeLimitBuyOrder(EClientSocket eClientSocket, Contract stock) {

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
