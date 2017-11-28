package model;

import java.util.ArrayList;

import model.exceptions.DuplicateContractException;

/**
 * Stores a list of unique ContractWithPriceDetail which are to be monitored with live stream of price data
 */
public class UniqueContractList {
    private final ArrayList <ContractWithPriceDetail> contractWithPriceDetailArrayListArrayList;

    public UniqueContractList() {
        contractWithPriceDetailArrayListArrayList = new ArrayList<>();
    }

    public UniqueContractList(int maxNumberOfContracts) {
        contractWithPriceDetailArrayListArrayList = new ArrayList<>(maxNumberOfContracts);
    }

    public void addContract(ContractWithPriceDetail contract) throws DuplicateContractException{
        if (contractWithPriceDetailArrayListArrayList.contains(contract)) {
            throw new DuplicateContractException();
        }
        contractWithPriceDetailArrayListArrayList.add(contract);
    }

    public void removeContract(ContractWithPriceDetail contract) {
        contractWithPriceDetailArrayListArrayList.remove(contract);
    }

    public ArrayList<ContractWithPriceDetail> getContractArrayWithPriceDetailList() {
        return contractWithPriceDetailArrayListArrayList;
    }
}
