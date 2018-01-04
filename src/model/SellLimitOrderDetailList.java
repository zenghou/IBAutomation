package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

/** Stores {@see SellLimitOrderDetail} objects */
public class SellLimitOrderDetailList {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Keeps track of the list of order ids that are sell orders
     */
    private ArrayList<Integer> listOfSellOrderIds;
    private ArrayList<SellLimitOrderDetail> sellLimitOrderDetailsArrayList;

    public SellLimitOrderDetailList() {
        sellLimitOrderDetailsArrayList = new ArrayList<>();
        listOfSellOrderIds = new ArrayList<>();
    }

    /**
     * Takes in a {@see sellLimitOrderDetail} and adds it to the {@see sellLimitOrderDetailsArrayList}, or updates
     * the existing sellLimitOrderDetail accordingly (This situation happens when orders are partially filled).
     */
    public void updateListWith (SellLimitOrderDetail sellLimitOrderDetail) {
        for (SellLimitOrderDetail eachOrderDetail : sellLimitOrderDetailsArrayList) {
            if (isExistingSellLimitOrderDetail(eachOrderDetail, sellLimitOrderDetail)) {
                double additionalSharesFilled = sellLimitOrderDetail.getFilled();
                eachOrderDetail.fillMoreShares(additionalSharesFilled);

                LOGGER.info("Updated existing SellLimitOrderDetail for " + sellLimitOrderDetail.getOrderId() +
                        ": " + sellLimitOrderDetail.getFilled() + " additional shares filled, " +
                        eachOrderDetail.getRemaining() + " shares remaining. Order fully filled: " +
                        eachOrderDetail.isFullyFilled());
            }
        }
        addSellLimitOrderDetail(sellLimitOrderDetail);

        LOGGER.info("Added new SellLimitOrderDetail. Order Id " + sellLimitOrderDetail.getOrderId() +
                ": " + sellLimitOrderDetail.getFilled() + " shares filled, " +
                sellLimitOrderDetail.getRemaining() + " shares remaining. Order fully filled: " +
                sellLimitOrderDetail.isFullyFilled());
    }

    /**
     * Private method that takes in a {@see sellLimitOrderDetail} and only adds it to the
     * {@see sellLimitOrderDetailsArrayList}. Used by {@see SellLimitOrderDetailList#updateListWith}
     */
    private void addSellLimitOrderDetail (SellLimitOrderDetail sellLimitOrderDetail) {
        if (sellLimitOrderDetailsArrayList.contains(sellLimitOrderDetail)) {
            System.out.println("SellOrderDetailsList should not contain duplicates!");
            return;
        }

        sellLimitOrderDetailsArrayList.add(sellLimitOrderDetail);
    }

    /**
     * Takes in an {@code existingOrderDetail} and an {@code orderDetail} and checks if the latter is an existing
     * {@code SellLimitOrder}
     */
    private boolean isExistingSellLimitOrderDetail(SellLimitOrderDetail existingOrderDetail,
                                                   SellLimitOrderDetail orderDetail) {
        return (existingOrderDetail.matchesOrderId(orderDetail.getOrderId()));
    }

    /**
     * Takes in an {@code orderId} and adds it to the list of sell order Ids.
     */
    public void addSellOrderId(int orderId) {
        if (listOfSellOrderIds.contains(orderId)) {
            System.out.println("Sell Order ID list should not contain duplicates! Order ID: " + orderId + "should not be" +
                    "added");
            return;
        }
        listOfSellOrderIds.add(orderId);
    }

    public boolean isSellOrderId(int orderId) {
        return listOfSellOrderIds.contains(orderId);
    }

    /**
     * Returns a list of order Ids that are sell limit orders, which are not fully filled by a fixed time
     * {@see DateBuilder#getCancellationTime}
     */
    public ArrayList<Integer> getOrderIdsForUnfilledSellLimitOrders() {
        ArrayList<Integer> listOfOrderIds = new ArrayList<>();

        for (SellLimitOrderDetail eachOrderDetail : sellLimitOrderDetailsArrayList) {
            if (!eachOrderDetail.isFullyFilled()) {
                listOfOrderIds.add(eachOrderDetail.getOrderId());
            }
        }

        return listOfOrderIds;
    }

    /**
     * Returns a list of order Ids that are sell limit orders, which are not fully filled by a fixed time
     * {@see DateBuilder#getCancellationTime}
     */
    public SellLimitOrderDetail retrieveSellLimitOrderDetailById(int orderId) {
        for (SellLimitOrderDetail orderDetail : sellLimitOrderDetailsArrayList) {
            if (orderDetail.matchesOrderId(orderId)) {
                return orderDetail;
            }
        }
        return null;
    }

    /**
     * Returns a HashMap with a stock symbol being the key, and the number of remaining positions as the value. The
     * HashMap is used to determine which stocks, and their respective positions that should be closed with MOC.
     */
    public HashMap<String, Double> getSymbolsAndRemainingPositionsForUnfilledSellLimitOrders() {
        HashMap<String, Double> symbolsAndRemainingPositions = new HashMap<>();

        for (SellLimitOrderDetail eachOrderDetail : sellLimitOrderDetailsArrayList) {
            if (eachOrderDetail.hasToBeClosedByMOC()) {
                symbolsAndRemainingPositions.put(eachOrderDetail.getSymbol(), eachOrderDetail.getRemaining());
            }
        }
        return symbolsAndRemainingPositions;
    }

    /**
     * Removes an {@code orderId} from this list such that the callbacks for placeOrders in EWrapperImplementation will
     * be guarded against orderIds that are sellLimitOrderIds but have to be executed by MOC.
     */
    public void removeSellLimitOrderId(int orderId) throws Exception {
        if (listOfSellOrderIds.contains(new Integer(orderId))) {
            listOfSellOrderIds.remove(new Integer(orderId));
        } else {
            throw new Exception("Order ID: " + orderId + "not in sell limit order list!");
        }

    }
}
