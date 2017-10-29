package model;

import java.util.ArrayList;

import logic.ContractBuilder;

public class ModelManager implements Model {
    UniqueContractList uniqueContractList = new UniqueContractList();
    ArrayList<String> listOfSymbols = new ArrayList<>();

    @Override
    public void getListOfSymbols() {
        //TODO: read CSV/TXT file and get list of stocks
    }

    /**
     * Loops through all ticker symbols in {@code listOfSymbols}
     * and adds the created Contract object into {@code uniqueContractList}
     */
    public void createUniqueContractList() {
        for (String symbol: listOfSymbols) {
            uniqueContractList.addContract(ContractBuilder.buildStock(symbol));
        }
    }
}
