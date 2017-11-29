//@@author zenghou
package logic;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;

import model.ContractWithPriceDetail;

public interface Logic {
    /**
     * Get real-time stock prices
     */
    void getRealTimeBars() throws InterruptedException;

    /**
     * Takes in a {@code stock} whose current price is 16% below opening price
     * and executes a Limit Buy order.
     */
    void placeLimitBuyOrder(ContractWithPriceDetail contract);

    /**
     * Determines if stock's current price is below opening price by {@code percentage}
     */
    boolean hasFallenByPercentage(Contract contract, int percentage);

    /**
     * Gets the opening price of a stock symbol
     */
    double getOpeningPrice(String stockSymbol);

    /**
     * Cancels the current realtimebar subscription
     */
    void cancelRealTimeBars();
}
