//@@author zenghou
package model;

import java.util.ArrayList;

import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

/**
 * Stores a list of unique ContractWithPriceDetail which are to be monitored with live stream of price data
 */
public class UniqueContractList {
    private final int DEFAULT_ARRAY_SIZE = 1000;
    // default array size will be 1000
    private int arraySize = DEFAULT_ARRAY_SIZE;

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

    public void removeContract(ContractWithPriceDetail contract) {
        contractWithPriceDetailArrayList.remove(contract);
    }

    public ArrayList<ContractWithPriceDetail> getContractArrayWithPriceDetailList() {
        return contractWithPriceDetailArrayList;
    }
}
