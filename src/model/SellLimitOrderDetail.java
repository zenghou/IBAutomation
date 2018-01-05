package model;

import java.util.logging.Logger;

/**
 * Stores the details (orderId, shares filled, shares remaining) for a particular sell order when
 * {@see EWrapperImplementation#orderStatus()} is called. Object can be uniqued identified by the {@see orderId} assigned.
 */
public class SellLimitOrderDetail {
    private static final Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private int orderId;
    private double filled;
    private double remaining;
    private boolean isFullyFilled;
    private boolean hasSymbol;
    private String symbol;

    public SellLimitOrderDetail(int orderId, double filled, double remaining) {
        this.orderId = orderId;
        this.filled = filled;
        this.remaining = remaining;
        hasSymbol = false;
        symbol = "";

        if (remaining == 0.00) {
            isFullyFilled = true;
        } else {
            isFullyFilled = false;
        }
    }

    public int getOrderId() {
        return orderId;
    }

    public double getFilled() {
        return filled;
    }

    public double getRemaining() {
        return remaining;
    }

    /** Takes in an orderId and checks if the details correspond by verifying this object's orderId */
    public boolean matchesOrderId(int orderId) {
        return (this.orderId == orderId);
    }

    public void fillMoreShares(double additionalSharesFilled) {
        filled += additionalSharesFilled;
        reduceRemainingShares(additionalSharesFilled);
        if (remaining == 0.00) {
            isFullyFilled = true;
        } else {
            isFullyFilled = false;
        }
    }

    private void reduceRemainingShares(double additionalSharesFilled) {
        remaining -= additionalSharesFilled;
    }

    public boolean isFullyFilled() {
        return isFullyFilled;
    }

    public void setSymbol(String symbol) throws Exception {
        if (!hasSymbol)  {
            this.symbol = symbol;
            this.hasSymbol = true;

            LOGGER.info("=========================[ Symbol for sellLimitOrderDetail has been set to: " + symbol +
                    " ]=========================");
        } else {
            throw new Exception("SellLimitOrderDetail already has a symbol: " + symbol);
        }
    }

    public String getSymbol() {
        return symbol;
    }

    public boolean hasToBeClosedByMOC() {
        return hasSymbol && (getRemaining() > 0.00) && (!isFullyFilled);
    }
}
