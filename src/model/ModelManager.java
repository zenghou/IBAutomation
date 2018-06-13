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

    // keeps track of the first 100 contract read in from the CSV file
    private UniqueContractList contractListToBeMonitored;

    // keeps track of the 101th contract and onwards read in from the CSV file
    private UniqueContractList unmonitoredContractList;

    // stores a list of the contracts to be submitted for an order
    private UniqueOrderContractList uniqueOrderContractList;

    private UniqueContractList uniqueContractToCloseList;
    private SellLimitOrderDetailList sellLimitOrderDetailList;
    private OpenOrderDetailList openOrderDetailList;


    public ModelManager() {

        // instead of having uniqueContractLists, we will only have ONE uniqueContractList of max size 100
        contractListToBeMonitored = new UniqueContractList(100);
        unmonitoredContractList = new UniqueContractList(400);

        tickerPriceHashMap = new HashMap<>();

        // uniqueOrderContractList holds at most 250 contracts
        uniqueOrderContractList = new UniqueOrderContractList(250);

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
        createUniqueContractLists();
    }

    /** Gives {@see Parser} access to tickerPriceHashMap to populate with stock symbols */
    @Override
    public HashMap<String, Double> getTickerPriceHashMap() {
        return tickerPriceHashMap;
    }

    @Override
    public UniqueContractList getUniqueContractListToBeMonitored() {
        return contractListToBeMonitored;
    }

    @Override
    public UniqueContractList getUnmonitoredContractList() { return unmonitoredContractList; }

    @Override
    public UniqueContractList getUniqueContractToCloseList() {
        return uniqueContractToCloseList;
    }

    @Override
    public ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId) {
        ContractWithPriceDetail contractWithPriceDetail = null;
        contractWithPriceDetail = contractListToBeMonitored.retrieveContractByRequestId(reqId);
        assert(contractWithPriceDetail != null);
        return contractWithPriceDetail;
    }

    /**
     * Loops through all ticker and price key-value pairs in {@code tickerPriceHashMap}
     * and adds the created {@see ContractWithPriceDetail} object into {@code uniqueOrderContractList}.
     * First 100 is monitored and remaining stocks are added to a separate unmonitored list.
     * Called only by {@link #initializeModel()}
     */
    private void createUniqueContractLists() {
        int numberOfContractsToBeMonitored = 0;
        try {
            // tickerPriceHasMap is guaranteed to be unique
            for (Map.Entry<String, Double> entry: tickerPriceHashMap.entrySet()) {
                numberOfContractsToBeMonitored += 1;

                String ticker = entry.getKey();
                Double price = entry.getValue();
                ContractWithPriceDetail newContract = ContractBuilder.buildContractWithPriceDetail(ticker, price);
                if (numberOfContractsToBeMonitored <= 100) {
                    contractListToBeMonitored.addContract(newContract);
                } else {
                    // Add to unmonitoredList
                    unmonitoredContractList.addContract(newContract);
                }
            }
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
        } catch (FullContractListException fcle) {
            System.out.println(fcle.getMessage() + "\n" + "Additional contracts should not be added to a full" +
                    "Contract list");
        }
    }

    @Override
    public void addContractToOrderList(ContractWithPriceDetail contract) {
        try {
            // whenever a new ticker is added to the ticker hashmap, we will immediately add it to the uniqueorderContractList
            // so that the contract gets added immediately.
            // I'm assuming that the oneAndOnlyUniqueContractList will always be filled (i.e. there is a starting of 100 symbols in the CSV file)
            uniqueOrderContractList.addContract(contract);
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
    public boolean isBuyOrder(int orderId) {
        return uniqueOrderContractList.containsOrderId(orderId);
    }

    @Override
    public String retrieveSymbolByBuyOrderId(int orderId) {
        return uniqueOrderContractList.retrieveSymbolByOrderId(orderId);
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
