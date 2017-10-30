package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import model.Model;

public class LogicManager implements Logic {
    private Model model;
    private Parser parser;

    public LogicManager(Model modelManager) {
        model = modelManager;
        parser = new Parser("/Users/ZengHou/Desktop/testStockList.csv", model);
        parser.readDataUpdateModel();
        model.initializeModel(); // called after listOfSymbol is populated
    }

    /**
     * Loops through model's contract list and retrieves realtimebars for each stock inside
     * @param eClientSocket to transmit request message from client to TWS server
     * @throws InterruptedException
     */
    @Override
    public void getRealTimeBars(EClientSocket eClientSocket) throws InterruptedException {

        for (Contract contract: model.getViewOnlyContractList()) {
            eClientSocket.reqRealTimeBars(3001, contract, 5, "MIDPOINT",
                    true, null);
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
