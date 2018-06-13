//@@author zenghou
package logic;

import java.util.Observer;

import com.ib.client.Contract;
import com.ib.client.Order;

import model.ContractWithPriceDetail;

public interface Logic extends Observer {

    /**
     * Requests for market data (to access OLHC)
     */
    void getRealTimeMarketData();

    /**
     * Takes in a {@code stock} whose current price is {@code percentageBelow} opening price
     * and executes a Limit Buy order of a maximum number of shares with {@code sum} amount of money.
     */
    void placeLimitBuyOrder(ContractWithPriceDetail contract, double percentageBelow, double sum);

    /**
     * Cancels the real time bar subscription for {@code ContractWithPriceDetail}
     */
    void cancelRealTimeBarsForContract(ContractWithPriceDetail contract);

    /**
     * Cancels the reqMktData subscription for {@code ContractWithPriceDetail}
     */
    void cancelRealTImeMarketDataForContract(ContractWithPriceDetail contract);

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
     * Loops through Model's {@see uniqueContractToCloseList} and creates a limit sell order for each Contract at
     * {@code percentageAboveOrderPrice } above market price. This method should be called when the program first
     * executes in order to close previous day's active positions.
     */
    void setLimitSellOrdersForAllExistingPositions(double percentageAboveOrderPrice);

    /**
     * Takes in {@param orderId} of an open order and cancels it.
     */
    void cancelOrder(int orderId);

    int getCurrentOrderId();

    void incrementOrderId();

    void placeOrder(int currentOrderId, Contract contract, Order order);

    void placeLimitBuyOrderForUnmonitoredContracts();

    ScheduledMarketOnClose getScheduledMarketOnCloseTask();

    ScheduledCancelUnfilledOrders getScheduledCancelUnfilledOrdersTask();
}
