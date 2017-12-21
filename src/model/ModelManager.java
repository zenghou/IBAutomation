//@@author zenghou
package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

public class ModelManager implements Model {
    /** List of symbols prepared after {@code Parser} reads the csv file */
    private HashMap<String, Double> tickerPriceHashMap;
    private UniqueContractList uniqueContractList;
    private UniqueOrderContractList uniqueOrderContractList;

    private UniqueContractList uniqueContractToCloseList;

    public ModelManager() {
        uniqueContractList = new UniqueContractList();
        tickerPriceHashMap = new HashMap<>();

        // uniqueOrderContractList holds at most 15 contracts
        uniqueOrderContractList = new UniqueOrderContractList(15);

        uniqueContractToCloseList = new UniqueContractList();
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
    public UniqueContractList getUniqueContractList() {
        return uniqueContractList;
    }

    @Override
    public UniqueContractList getUniqueContractToCloseList() {
        return uniqueContractToCloseList;
    }

    @Override
    public ArrayList<ContractWithPriceDetail> getViewOnlyContractWithPriceDetailList() {
        return uniqueContractList.getContractArrayWithPriceDetailList();
    }

    @Override
    public ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId) {
        ContractWithPriceDetail contractWithPriceDetail = null;
        for (ContractWithPriceDetail contract: uniqueContractList
                .getContractArrayWithPriceDetailList()) {
            if (contract.getRequestId() == reqId) {
                contractWithPriceDetail = contract;
                break;
            }
        }
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
                uniqueContractList.addContract(ContractBuilder.buildContractWithPriceDetail(ticker, price));
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
            uniqueContractList.updateContractList(contract);
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
    public UniqueOrderContractList getUniqueOrderContractList() {
        return uniqueOrderContractList;
    }
}
