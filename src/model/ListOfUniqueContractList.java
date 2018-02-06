package model;

import java.util.ArrayList;

import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

/**
 * Keeps track of each {@see UniqueContractList}, of which each has a maximum number contracts. This class should facilitate
 * the rotating of each "batch" of UniqueContractList (i.e. the maximum number).
 */
public class ListOfUniqueContractList {
    private ArrayList<UniqueContractList> uniqueContractLists;
    // index of the next UniqueContractList that is not fully filled
    private int indexForNextAvailableList;

    // current number of UniqueContractList objects stored
    private int numberOfUniqueContractList;

    // external
    private int indexForNextUniqueContractList;

    public ListOfUniqueContractList() {
        // initialize ListOfUniqueContractLst with one UniqueContractList
        uniqueContractLists = new ArrayList<>();
        uniqueContractLists.add(new UniqueContractList());
        indexForNextAvailableList = 0;
        numberOfUniqueContractList = 1;

        // start from the first list
        indexForNextUniqueContractList = 0;
    }

    public void addContract(ContractWithPriceDetail contract) throws FullContractListException, DuplicateContractException {
         if (isCurrentListFull()) {
             // only add new UniqueContractList when current one is full
             addNewUniqueContractList();
         }

         UniqueContractList currentList = getListByIndex(indexForNextAvailableList);
         currentList.addContract(contract);
    }

    private void addNewUniqueContractList() {
        // add new UniqueContractList
        uniqueContractLists.add(new UniqueContractList());

        // increment index and number of unique contract list
        indexForNextAvailableList++;
        numberOfUniqueContractList++;
    }

    public boolean isCurrentListFull() {
        UniqueContractList currentList = getListByIndex(indexForNextAvailableList);
        return currentList.isFull();
    }

    private UniqueContractList getListByIndex(int index) {
        return uniqueContractLists.get(index);
    }

    public int getSize() {
        return numberOfUniqueContractList;
    }

    /**
     * Removes a {@param contract} after it has been submitted for an order
     * @return true if the Contract has been removed, otherwise false
     */
    public boolean removeContractFromList(ContractWithPriceDetail contract) {
        for (int i = 0; i < numberOfUniqueContractList; i++) {
            UniqueContractList currentList = getListByIndex(i);
            if (currentList.containsContract(contract)) {
                currentList.removeContract(contract);
                return true;
            }
        }

        return false;
    }

    public boolean containsContract(ContractWithPriceDetail contract) {
        for (UniqueContractList eachList : uniqueContractLists) {
            if (eachList.containsContract(contract)) {
                return true;
            }
        }
        return false;
    }

    private UniqueContractList getFirstAvailableList() {
        return uniqueContractLists.get(indexForNextAvailableList);
    }

    public void updateListOfUniqueContractList(ContractWithPriceDetail contract) throws FullContractListException,
            DuplicateContractException {
        if (!this.containsContract(contract)) {
            UniqueContractList uniqueContractList = getFirstAvailableList();
            uniqueContractList.updateContractList(contract);
        }
    }

    public ArrayList<UniqueContractList> getArrayList() {
        return this.uniqueContractLists;
    }

    public ContractWithPriceDetail retrieveContractByRequestId(int reqId) {
        for (UniqueContractList eachList : uniqueContractLists) {
            for (ContractWithPriceDetail contract: eachList
                    .getContractArrayWithPriceDetailList()) {
                if (contract.getRequestId() == reqId) {
                    return contract;
                }
            }
        }
        return null;
    }

    public UniqueContractList getNextUniqueContractList() {
        UniqueContractList contractList = getListByIndex(indexForNextUniqueContractList);

        incrementIndexForNextUniqueContractList();

        return contractList;
    }

    private void incrementIndexForNextUniqueContractList() {
        indexForNextUniqueContractList++;

        if (indexForNextUniqueContractList % getSize() == 0) {
            indexForNextUniqueContractList = 0;
        }
    }
}
