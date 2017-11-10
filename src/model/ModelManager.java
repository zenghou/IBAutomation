package model;

import java.util.ArrayList;

import com.ib.client.Contract;

import logic.ContractBuilder;
import model.exceptions.DuplicateContractException;

public class ModelManager implements Model {
    /** List of symbols prepared after {@code Parser} reads the csv file */
    private ArrayList<String> listOfSymbols;
    private ArrayList<StockPriceProperty> listOfStockPriceProperties;
    private UniqueContractList uniqueContractList;

    public ModelManager() {
        uniqueContractList = new UniqueContractList();
        listOfSymbols = new ArrayList<>();
        listOfStockPriceProperties = new ArrayList<>();
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
    public ArrayList<Contract> getViewOnlyContractList() {
        return uniqueContractList.getContractArrayList();
    }

    /**
     * Loops through all ticker symbols in {@code listOfSymbols}
     * and adds the created Contract object into {@code uniqueContractList}.
     * Called only by {@link #initializeModel()}
     */
    private void createUniqueContractList() {
        try {
            for (String symbol : listOfSymbols) {
                uniqueContractList.addContract(ContractBuilder.buildStock(symbol));
            }
        } catch (DuplicateContractException dce) {
            System.out.println(dce.getMessage() + "\n" + "There should not be any duplicate symbols");
        }
    }
}
