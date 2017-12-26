//@@author zenghou
package logic;

import java.util.Observer;

import model.ContractWithPriceDetail;

public interface Logic extends Observer {
    /**
     * Get real-time stock prices
     */
    void getRealTimeBars() throws InterruptedException;

    /**
     * Takes in a {@code stock} whose current price is {@code percentageBelow} opening price
     * and executes a Limit Buy order of a maximum number of shares with {@code sum} amount of money.
     */
    void placeLimitBuyOrder(ContractWithPriceDetail contract, double percentageBelow, double sum);

    /**
     * Cancels the real time bar subscription for {@code ContractWithPriceDetail}
     */
    void cancelRealTimeBarsForContract(ContractWithPriceDetail contract);

    /** Returns the Parser object used */
    Parser getParser();

    /**
     * Request for information about current portfolio. Callback for this method will add active stocks in the portfolio
     * to Model's {@see uniqueContractToCloseList}
     */
    void requestAccountUpdates();

    /**
     *  Cancels subscription information about current portfolio.
     */
    void cancelAccountUpdates();

    /**
     * Loops through Model's {@see uniqueContractToCloseList} and sells each Contract at market price. This method should
     * be called when the program first executes in order to close previous day's active positions.
     */
    void closeAllActivePositionsAtMarketOpen();
}
