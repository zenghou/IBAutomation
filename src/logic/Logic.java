//@@author zenghou
package logic;

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
     * Cancels the current realtimebar subscription
     */
    void cancelRealTimeBars();
}
