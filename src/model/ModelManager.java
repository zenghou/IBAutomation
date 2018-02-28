//@@author zenghou
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Events.EventManager;
import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

public class ModelManager extends EventManager implements Model {
    /** List of symbols prepared after {@code Parser} reads the csv file */
    private HashMap<String, Double> tickerPriceHashMap;

    // keeps track of all the contracts read in from the CSV file
    private ListOfUniqueContractList uniqueContractLists;

    // stores a list of the contracts to be submitted for an order
    private UniqueOrderContractList uniqueOrderContractList;

    private UniqueContractList uniqueContractToCloseList;
    private SellLimitOrderDetailList sellLimitOrderDetailList;
    private OpenOrderDetailList openOrderDetailList;

    public ModelManager() {
        uniqueContractLists = new ListOfUniqueContractList();

        tickerPriceHashMap = new HashMap<>();

        // uniqueOrderContractList holds at most 150 contracts
        uniqueOrderContractList = new UniqueOrderContractList(150);

        uniqueContractToCloseList = new UniqueContractList();

        sellLimitOrderDetailList = new SellLimitOrderDetailList();

        openOrderDetailList = new OpenOrderDetailList();
    }

    /**
     * Prepares the Model class by populating uniqueContractList and tickerPriceHashMap.
     * Can only be called when {@code tickerPriceHashMap} is prepared.
     */
    @Override
    public void initializeModel() {
        createUniqueContractList();
    }

    /** Gives {@see Parser} access to tickerPriceHashMap to populate with stock symbols */
    @Override
    public HashMap<String, Double> getTickerPriceHashMap() {
        return tickerPriceHashMap;
    }

    @Override
    public ListOfUniqueContractList getUniqueContractLists() {
        return uniqueContractLists;
    }

    @Override
    public UniqueContractList getUniqueContractToCloseList() {
        return uniqueContractToCloseList;
    }

    @Override
    public ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId) {
        ContractWithPriceDetail contractWithPriceDetail = null;
        contractWithPriceDetail = uniqueContractLists.retrieveContractByRequestId(reqId);
        assert(contractWithPriceDetail != null);
        return contractWithPriceDetail;
    }

    /**
     * Loops through all ticker and price key-value pairs in {@code tickerPriceHashMap}
     * and adds the created {@see ContractWithPriceDetail} object into {@code uniqueOrderContractList}.
     * Called only by {@link #initializeModel()}
     */
    private void createUniqueContractList() {
        try {
            for (Map.Entry<String, Double> entry: tickerPriceHashMap.entrySet()) {

                String ticker = entry.getKey();
                Double price = entry.getValue();
                uniqueContractLists.addContract(ContractBuilder.buildContractWithPriceDetail(ticker, price));
            }
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
        } catch (FullContractListException fcle) {
            System.out.println(fcle.getMessage() + "\n" + "Additional contracts should not be added to a full" +
                    "Contract list");
        }
    }

    @Override
    public void updateUniqueContractList(ContractWithPriceDetail contract) {
        try {
            //TODO: change to update contract method (to use observer/observable)
            uniqueContractLists.updateListOfUniqueContractList(contract);
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate for " + contract.symbol());
        } catch (FullContractListException fcle) {
            System.out.println(fcle.getMessage() + "\n" + "Additional contracts should not be added to a full" +
                    "Contract list");
        }
    }

    /**
     * Adds a {@link ContractWithPriceDetail} to {@link UniqueOrderContractList} in ModelManager after it is checked
     * to be ready for order submission {@link ContractWithPriceDetail#hasFallenBelowPercentage(double)}.
     */
    public void addContractWithPriceDetailToOrderList(ContractWithPriceDetail contractWithPriceDetail) {
        // TODO: check for size of OrderList; if maximum size, stop requesting for data.
        try {
            uniqueOrderContractList.addContract(contractWithPriceDetail);
        } catch (DuplicateContractException dce) {
            // TODO: Remove this temp print log
            uniqueOrderContractList.printAll();
            System.out.println(dce.getMessage() + "\n" + "Duplicate symbol " + contractWithPriceDetail.symbol() +
                    "should not be added!");
        } catch (FullContractListException fcle) {
            System.out.println(fcle.getMessage()+ "\n" + "Additional contracts should not be added to a full" +
                    "Contract list");
        }
    }

    @Override
    public boolean hasSentOrderForContract(ContractWithPriceDetail contractWithPriceDetail) {
        return uniqueOrderContractList.containsContract(contractWithPriceDetail);
    }

    @Override
    public UniqueOrderContractList getUniqueOrderContractList() {
        return uniqueOrderContractList;
    }

    @Override
    public void updateSellLimitOrderDetailList(SellLimitOrderDetail sellLimitOrderDetail) {
        sellLimitOrderDetailList.updateListWith(sellLimitOrderDetail);
    }

    @Override
    public boolean isSellLimitOrderId(int orderId) {
        return sellLimitOrderDetailList.isSellOrderId(orderId);
    }

    @Override
    public void addSellLimitOrderId(int orderId) {
        sellLimitOrderDetailList.addSellOrderId(orderId);
    }

    @Override
    public void removeSellLimitOrderId(int orderId) throws Exception {
        sellLimitOrderDetailList.removeSellLimitOrderId(orderId);
    }

    @Override
    public ArrayList<Integer> getOrderIdsForUnfilledSellLimitOrders() {
        return sellLimitOrderDetailList.getOrderIdsForUnfilledSellLimitOrders();
    }

    @Override
    public HashMap<String, Double> getSymbolAndPositionsForUnfilledSellLimitOrders() {
        return sellLimitOrderDetailList.getSymbolsAndRemainingPositionsForUnfilledSellLimitOrders();
    }

    @Override
    public void printAllStocksInUniqueContractToCloseList() {
        uniqueContractToCloseList.printAllSymbolsInList();
    }

    @Override
    public SellLimitOrderDetail retrieveSellLimitOrderDetailById(int orderId) {
        return sellLimitOrderDetailList.retrieveSellLimitOrderDetailById(orderId);
    }

    @Override
    public void addOpenOrderDetail(OpenOrderDetail openOrderDetail) {
        try {
            openOrderDetailList.add(openOrderDetail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String retrieveSymbolByOrderId(int orderId) {
        return openOrderDetailList.retrieveSymbolFromOrderId(orderId);
    }
}
