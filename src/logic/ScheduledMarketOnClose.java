package logic;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;
import java.util.logging.Logger;

import com.ib.client.Contract;
import com.ib.client.Order;

import model.ContractBuilder;
import model.Model;

/** A scheduled task that submits market on close orders for previous day's active positions at a set time */
public class ScheduledMarketOnClose extends TimerTask {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private Logic logic;
    private Model model;

    public ScheduledMarketOnClose(Logic logic, Model model) {
        this.logic = logic;
        this.model = model;
    }

    @Override
    public void run() {
        submitMarketOnCloseOrders();
    }

    private Order createMarketOnCloseOrder(int quantity) {
        Order order = new Order();
        order.action("SELL");
        order.orderType("MOC");
        order.totalQuantity(quantity);

        return order;
    }

    private void submitMarketOnCloseOrders() {
        HashMap<String,Double> symbolAndPositions = model.getSymbolAndPositionsForUnfilledSellLimitOrders();

        for (Map.Entry<String, Double> mapEntry : symbolAndPositions.entrySet()) {

            String symbol = mapEntry.getKey();
            double quantity = mapEntry.getValue();
            int quantityToBeClosed = (int) quantity;

            LOGGER.info("***************************[ Attempting to place MOC order for " + quantityToBeClosed +
                    "remaining positions of " + symbol + ". ]******************************");

            Order orderToBeSubmitted = createMarketOnCloseOrder(quantityToBeClosed);

            int currentOrderId = logic.getCurrentOrderId();

            Contract contract = ContractBuilder.buildContractWithSymbol(symbol);

            logic.placeOrder(currentOrderId, contract, orderToBeSubmitted);

            logic.incrementOrderId();

        }

    }
}
