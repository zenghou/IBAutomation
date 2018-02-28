//@@author zenghou
package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.logging.Logger;


import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

/**
 * Stores a list of unique ContractWithPriceDetail which are to be monitored with live stream of price data
 */
public class UniqueContractList extends Observable {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final int DEFAULT_ARRAY_SIZE = 80;
    private int arraySize = DEFAULT_ARRAY_SIZE;
    private int listNumber;

    private final ArrayList <ContractWithPriceDetail> contractWithPriceDetailArrayList;

    // initializes a contractWithPriceDetailArrayList that can hold 1000 contracts
    public UniqueContractList() {
        contractWithPriceDetailArrayList = new ArrayList<>(arraySize);
    }

    public UniqueContractList(int maxNumberOfContracts) {
        // replace default array size
        arraySize = maxNumberOfContracts;
        contractWithPriceDetailArrayList = new ArrayList<>(maxNumberOfContracts);
    }

    public void setListNumber(int listNumber) {
        this.listNumber = listNumber;
    }

    public int getListNumber() {
        return this.listNumber;
    }

    public void addContract(ContractWithPriceDetail contract) throws FullContractListException,
            DuplicateContractException {
        if (contractWithPriceDetailArrayList.size() == arraySize) {
            throw new FullContractListException();
        }
        if (contractWithPriceDetailArrayList.contains(contract)) {
            throw new DuplicateContractException();
        }
        contractWithPriceDetailArrayList.add(contract);
    }

    public void updateContractList(ContractWithPriceDetail contract) throws FullContractListException,
            DuplicateContractException {
        addContract(contract);

        // notify observers
        setChanged();
        notifyObservers(contract);
    }

    public void removeContract(ContractWithPriceDetail contract) {
        contractWithPriceDetailArrayList.remove(contract);
    }

    public ArrayList<ContractWithPriceDetail> getContractArrayWithPriceDetailList() {
        return contractWithPriceDetailArrayList;
    }

    public void printAllSymbolsInList() {

        LOGGER.info("=============================[ Retrieving all the symbols in this list: ");

        int numberOfSymbols = 1;

        for (ContractWithPriceDetail contract : contractWithPriceDetailArrayList) {
            LOGGER.info(numberOfSymbols + ": " + contract.symbol() + ", position: " + contract.getPosition());
            numberOfSymbols++;
        }
    }

    public ArrayList<String> getSortedListOfSymbols() {
        ArrayList<String> listOfSymbols = new ArrayList<>(DEFAULT_ARRAY_SIZE);
        for (ContractWithPriceDetail contract : contractWithPriceDetailArrayList) {
            listOfSymbols.add(contract.symbol());
        }
        Collections.sort(listOfSymbols);
        return listOfSymbols;
    }

    /**
     * Checks if this UniqueContractList is full
     */
    public boolean isFull() {
        return (this.contractWithPriceDetailArrayList.size() == DEFAULT_ARRAY_SIZE);
    }

    public boolean containsContract(ContractWithPriceDetail contract) {
        return this.contractWithPriceDetailArrayList.contains(contract);
    }

    public int size() {
        return this.contractWithPriceDetailArrayList.size();
    }

    public ArrayList<ContractWithPriceDetail> getInternalArray() {
        return this.contractWithPriceDetailArrayList;
    }
}
