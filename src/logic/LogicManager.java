package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

public class LogicManager implements Logic {
    @Override
    public void realTimeBars(EClientSocket eClientSocket, Contract stockContract) throws InterruptedException {
        System.out.println("Collecting real time bars");

        //TODO: change the tickerID
        eClientSocket.reqRealTimeBars(3001, stockContract, 5, "MIDPOINT",
                true, null);
    }

    @Override
    public void placeLimitBuyOrder(EClientSocket eClientSocket, Contract stock) {

    }

    @Override
    public boolean hasFallenByPercentage(int percentage) {
        return false;
    }

    @Override
    public double getOpeningPrice(String stockSymbol) {
        return 0;
    }
}
