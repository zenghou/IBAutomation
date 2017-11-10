package model;

import java.util.ArrayList;

import com.ib.client.Contract;

import model.exceptions.DuplicateContractException;

/**
 * Stores a list of unique ContractWithPriceDetail which are to be monitored with live stream of price data
 */
public class UniqueContractWithPriceDetailList {
    private final ArrayList <ContractWithPriceDetail> contractWithPriceDetailArrayListArrayList;

    public UniqueContractWithPriceDetailList() {
        contractWithPriceDetailArrayListArrayList = new ArrayList<>();
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
