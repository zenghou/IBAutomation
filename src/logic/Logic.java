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
     * Cancels the all current real time bar subscription
     */
    void cancelRealTimeBars();

    /**
     * Cancels the real time bar subscription for {@code ContractWithPriceDetail}
     */
    void cancelRealTimeBarsForContract(ContractWithPriceDetail contract);

}
