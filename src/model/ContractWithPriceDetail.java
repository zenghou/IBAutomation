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
    private double currentPrice; // current market price of the stock
    private double position; // number of shares
    private double averageCost;

    public ContractWithPriceDetail() {
        super();

        this.dayOpeningPrice = DEFAULT_INVALID_VALUE;
        this.percentage = DEFAULT_INVALID_VALUE;

        requestId = (int) DEFAULT_INVALID_VALUE;
        currentPrice = DEFAULT_INVALID_VALUE;
        position = DEFAULT_INVALID_VALUE;
        averageCost = DEFAULT_INVALID_VALUE;
    }

    public ContractWithPriceDetail(double dayOpeningPrice, double percentage) {
        super();

        this.dayOpeningPrice = dayOpeningPrice;
        this.percentage = percentage;

        requestId = (int) DEFAULT_INVALID_VALUE;
        currentPrice = DEFAULT_INVALID_VALUE;
        position = DEFAULT_INVALID_VALUE;
        averageCost = DEFAULT_INVALID_VALUE;
    }

    /**
     * Replaces the default percentage with a new percentage. Used only when risk adjustments change.
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public void setCurrentPrice(double currentPrice) throws Exception {
        if (this.currentPrice == DEFAULT_INVALID_VALUE) {
            this.currentPrice = currentPrice;
        } else {
            throw new Exception("Current price for " + symbol() + " can only be set once!");
        }
    }

    /**
     * Sets the number of shares purchased for this contract. Only able to set the number of shares once.
     */
    public void setPosition(double position) throws Exception {
        if (this.position == DEFAULT_INVALID_VALUE) {
            this.position = position;
        } else {
            System.out.println("Number of shares for " + symbol() + "is " + position);
            throw new Exception("Position for " + symbol() + " can only be set once!");
        }
    }

    /**
     * Sets the average cost for this contract. Only able to set the average cost once.
     */
    public void setAverageCost(double averageCost) throws Exception {
        if (this.averageCost == DEFAULT_INVALID_VALUE) {
            this.averageCost = averageCost;
        } else {
            throw new Exception("Average cost of " + symbol() + " can only be set once!");
        }
    }

    /**
     * Assigns this Contract with a request Id after realTimeBars is called. Only able to set the requestId once.
     * Guaranteed to be immutable.
     */
    public void setRequestId(int requestId) {
        this.requestId = requestId;
    }

    public boolean hasRequestId() {
        return (this.requestId != -1);
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

    public double getAverageCost() {
        return averageCost;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ContractWithPriceDetail) {
            ContractWithPriceDetail toBeCompared = (ContractWithPriceDetail) other;
            return super.equals(other) && (toBeCompared.getRequestId() == this.getRequestId());

        }
        return false;
    }
}
