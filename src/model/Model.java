//@@author zenghou
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * The API of the model component
 */
public interface Model {
    /** Returns a UniqueContractList object */
    UniqueContractList getUniqueContractListToBeMonitored();

    /** Sets up a model with a {@see listOfSymbols} and {@see UniqueContractList} */
    void initializeModel();

    /** Returns an String Double Hash Map for the {@see Parser} to populate with ticker and prices */
    HashMap<String, Double> getTickerPriceHashMap();

    /** Retrieves a ContractWithPriceDetails by requestId and returns it to caller*/
    ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId);

    /** Returns a UniqueOrderContractList object */
    UniqueOrderContractList getUniqueOrderContractList();

    /** Adds a contract that is ready for purchase order to the UniqueOrderContractList */
    void addContractWithPriceDetailToOrderList(ContractWithPriceDetail contractWithPriceDetail);

    void addContractToOrderList(ContractWithPriceDetail contract);

    UniqueContractList getUniqueContractToCloseList();

    void updateSellLimitOrderDetailList(SellLimitOrderDetail sellLimitOrderDetail);

    /** Checks if an orderId is a sell order */
    boolean isSellLimitOrderId(int orderId);

    boolean isBuyOrder(int orderId);

    String retrieveSymbolByBuyOrderId(int orderId);

    /** Keeps track of the order Ids that are sell orders */
    void addSellLimitOrderId(int orderId);

    /** Removes sell limit order ids after they are cancelled */
    void removeSellLimitOrderId(int orderId) throws Exception;

    /** Returns a list of orderIds whose orders are not filled such that they can be cancelled */
    ArrayList<Integer> getOrderIdsForUnfilledSellLimitOrders();

    /** Returns a HashMap of symbol and positions to be closed by MOC */
    HashMap<String, Double> getSymbolAndPositionsForUnfilledSellLimitOrders();

    /** Prints out a list of stocks in UniqueContractToCloselist to make sure the correct stocks are inside */
    void printAllStocksInUniqueContractToCloseList();

    SellLimitOrderDetail retrieveSellLimitOrderDetailById(int orderId);

    void addOpenOrderDetail(OpenOrderDetail openOrderDetail);

    String retrieveSymbolByOrderId(int orderId);

    boolean hasSentOrderForContract(ContractWithPriceDetail contractWithPriceDetail);

    UniqueContractList getUnmonitoredContractList();
}
