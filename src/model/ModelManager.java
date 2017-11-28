package model;

import java.util.ArrayList;

import logic.ContractBuilder;
import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

public class ModelManager implements Model {
    /** List of symbols prepared after {@code Parser} reads the csv file */
    private ArrayList<String> listOfSymbols;
    private UniqueContractList uniqueContractList;
    private UniqueOrderContractList uniqueOrderContractList;

    public ModelManager() {
        uniqueContractList = new UniqueContractList();
        listOfSymbols = new ArrayList<>();

        // uniqueOrderContractList holds at most 15 contracts
        uniqueOrderContractList = new UniqueOrderContractList(15);
    }

    /**
     * Prepares the Model class by populating uniqueContractList and listOfSymbols.
     * Can only be called when {@code listOfSymbols} is prepared.
     */
    @Override
    public void initializeModel() {
        createUniqueContractList();
    }

    /** Gives {@see Parser} access to listOfSymbols to populate with stock symbols */
    @Override
    public ArrayList<String> getListOfSymbolsArray() {
        return listOfSymbols;
    }

    @Override
    public UniqueContractList getUniqueContractList() {
        return uniqueContractList;
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
     * Loops through all ticker symbols in {@code listOfSymbols}
     * and adds the created Contract object into {@code uniqueContractList}.
     * Called only by {@link #initializeModel()}
     */
    private void createUniqueContractList() {
        try {
            for (String symbol : listOfSymbols) {
                uniqueContractList.addContract(ContractBuilder.buildContractWithPriceDetail(symbol));
            }
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
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
        try {
            uniqueOrderContractList.addContract(contractWithPriceDetail);
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
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
