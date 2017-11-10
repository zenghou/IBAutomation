package model;

import java.util.ArrayList;

import com.ib.client.Contract;

import logic.ContractBuilder;
import model.exceptions.DuplicateContractException;

public class ModelManager implements Model {
    /** List of symbols prepared after {@code Parser} reads the csv file */
    private ArrayList<String> listOfSymbols;
    private ArrayList<StockPriceProperty> listOfStockPriceProperties;
    private UniqueContractWithPriceDetailList uniqueContractWithPriceDetailList;

    public ModelManager() {
        uniqueContractWithPriceDetailList = new UniqueContractWithPriceDetailList();
        listOfSymbols = new ArrayList<>();
        listOfStockPriceProperties = new ArrayList<>();
    }

    /**
     * Prepares the Model class by populating uniqueContractWithPriceDetailList and listOfSymbols.
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
    public UniqueContractWithPriceDetailList getUniqueContractWithPriceDetailList() {
        return uniqueContractWithPriceDetailList;
    }

    @Override
    public ArrayList<ContractWithPriceDetail> getViewOnlyContractWithPriceDetailList() {
        return uniqueContractWithPriceDetailList.getContractArrayWithPriceDetailList();
    }

    /**
     * Loops through all ticker symbols in {@code listOfSymbols}
     * and adds the created Contract object into {@code uniqueContractWithPriceDetailList}.
     * Called only by {@link #initializeModel()}
     */
    private void createUniqueContractList() {
        try {
            for (String symbol : listOfSymbols) {
                uniqueContractWithPriceDetailList.addContract(ContractBuilder.buildContractWithPriceDetail(symbol));
            }
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
        }
    }
}
