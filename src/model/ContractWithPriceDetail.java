//@@author zenghou
package model;

import com.ib.client.Contract;

/**
 * Essentially a {@see Contract} with day's opening price
 */
public class ContractWithPriceDetail extends Contract{
    private static final double DEFAULT_INVALID_VALUE = -1.00;

    private double dayOpeningPrice;
    private double percentage;
    private int requestId;
    private double currentPrice;
    private double position; // number of shares

    public ContractWithPriceDetail() {
        super();

        this.dayOpeningPrice = DEFAULT_INVALID_VALUE;
        this.percentage = DEFAULT_INVALID_VALUE;

        requestId = (int) DEFAULT_INVALID_VALUE;
        currentPrice = DEFAULT_INVALID_VALUE;
        position = DEFAULT_INVALID_VALUE;
    }

    public ContractWithPriceDetail(double dayOpeningPrice, double percentage) {
        super();

        this.dayOpeningPrice = dayOpeningPrice;
        this.percentage = percentage;

        requestId = (int) DEFAULT_INVALID_VALUE;
        currentPrice = DEFAULT_INVALID_VALUE;
        position = DEFAULT_INVALID_VALUE;
    }

    /**
     * Replaces the default percentage with a new percentage. Used only when risk adjustments change.
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    /**
     * Assigns this Contract with a request Id after realTimeBars is called. Only able to set the requestId once.
     * Guaranteed to be immutable.
     */
    public void setRequestId(int requestId) throws Exception {
        if (this.requestId != -1) {
            throw new Exception("Cannot change requestId!");
        }
        this.requestId = requestId;
    }

    /**
     * Takes in the current price of the stock and checks if this Contract's stock has fallen below a certain
     * percentage. Returns true if price has fallen, which indicates that stock is ready for purchase.
     */
    public boolean hasFallenBelowPercentage(double currentPrice) {
        // current price has to be positive
        assert(currentPrice > 0);

        if (currentPrice >= dayOpeningPrice) {
            return false;
        }

        // set current price to the latest price
        this.currentPrice = currentPrice;

        // Percentage decrease will be stored as a positive value. In other words, when a stock with day's opening
        // price of $1.00 falls to $0.70, the percentage decrease will be 30%.
        double percentageDecrease = ((dayOpeningPrice - currentPrice)/dayOpeningPrice) * 100.00;
        if (percentageDecrease > percentage) {
            return true;
        }
        return false;
    }

    public int getRequestId() {
        return requestId;
    }

    public double getDayOpeningPrice() {
        return dayOpeningPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public double getPosition() {
        return position;
    }
}
