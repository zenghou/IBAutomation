package logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.TimerTask;
import java.util.logging.Logger;

import model.Model;

/** A scheduled task that cancels all unfilled sell limit orders at a set time */
public class ScheduledCancelUnfilledOrders extends TimerTask {
    private Logic logic;
    private Model model;

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ScheduledCancelUnfilledOrders(Logic logic, Model model) {
        this.logic = logic;
        this.model = model;
    }

    @Override
    public void run() {
        Date timeNow = new Date();
        LOGGER.info("======================== [ Executing Scheduled Cancellation of Unfilled Orders at " + timeNow +
                " ] ========================");

        ArrayList<Integer> orderIdsForCancellation = model.getOrderIdsForUnfilledSellLimitOrders();
        cancelUnfilledSellLimitOrders(orderIdsForCancellation);
    }

    private void cancelUnfilledSellLimitOrders(ArrayList<Integer> orderIds) {
        for (Integer orderId : orderIds) {
            LOGGER.info("***************************[ Attempting to cancel sell limit order id: " + orderId + " ]***" +
                    "***************************");
            logic.cancelOrder(orderId);

            LOGGER.info("===========================[ Attempting to remove order id: " + orderId + " ===" +
                    "===========================");

            try {
                model.removeSellLimitOrderId(orderId);
            } catch (Exception e) {
                e.printStackTrace();
            }

            LOGGER.info("===========================[ Order id: " + orderId + " removed from sellLimitOrderList!" +
                    " ]==============================");
        }
    }
}
