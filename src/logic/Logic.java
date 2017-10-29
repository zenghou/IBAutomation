package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

public interface Logic {
    /**
     * Get real-time stock prices
     */
    void realTimeBars(EClientSocket eClientSocket, Contract stockContract) throws InterruptedException;

    /**
     * Takes in a {@code stock} whose current price is 16% below opening price
     * and executes a Limit Buy order.
     */
    void placeLimitBuyOrder(EClientSocket eClientSocket, Contract stock);

    /**
     * Determines if stock's current price is below opening price by {@code percentage}
     */
    boolean hasFallenByPercentage(int percentage);

    /**
     * Gets the opening price of a stock symbol
     */
    double getOpeningPrice(String stockSymbol);
}
