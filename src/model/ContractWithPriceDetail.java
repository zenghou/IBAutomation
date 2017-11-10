package model;

import com.ib.client.Contract;

/**
 * Essentially a {@see Contract} with day's opening price
 */
public class ContractWithPriceDetail extends Contract{
    private double dayOpeningPrice;
    private boolean hasOpeningPrice;
    private double percentage;
    private int requestId;


    public ContractWithPriceDetail() {
        super(); // call Contract's constructor
        percentage = 16.00; // default is set at 16 percent
        dayOpeningPrice = 0.00; // no price is set
        hasOpeningPrice = false;
        requestId = -1; // default id which should be an invalid id
    }

    /**
     * Sets the opening price for the contract only if the current dayOpeningPrice is not set
     */
    public void setDayOpeningPrice(double dayOpeningPrice) throws Exception {
        if (hasOpeningPrice) {
            throw new Exception("Already has opening price!");
        }
        hasOpeningPrice = true;
        this.dayOpeningPrice = dayOpeningPrice;
    }

    /**
     * Replaces the default percentage with a new percentage. Used only when risk adjustments change.
     */
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    /**
     * Takes in the current price of the stock and checks if this Contract's stock has fallen below a certain
     * percentage. Returns true if price has fallen, which indicates that stock is ready for purchase.
     */
    public boolean hasFallenBelowPercentage(double currentPrice) {
        if (currentPrice >= dayOpeningPrice) {
            return false;
        }

        // Percentage decrease will be stored as a positive value. In other words, when a stock with day's opening
        // price of $1.00 falls to $0.70, the percentage decrease will be 30%.
        double percentageDecrease = ((dayOpeningPrice - currentPrice)/dayOpeningPrice) * 100;
        if (percentageDecrease > percentage) {
            return true;
        }
        return false;
    }

    public int getRequestId() {
        return requestId;
    }

    public boolean hasOpeningPrice() {
        return hasOpeningPrice;
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
}
