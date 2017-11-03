package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import model.Model;
import samples.testbed.contracts.ContractSamples;

public class LogicManager implements Logic {
    private Model model;
    private Parser parser;
    private int id = 1;

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
            System.out.println("Getting price for: " + contract.symbol());
            System.out.println("current id is: " + id);
            eClientSocket.reqRealTimeBars(id, contract, 5, "MIDPOINT",
                    true, null);
            id++;
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
