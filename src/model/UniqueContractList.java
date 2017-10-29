package model;

import java.util.ArrayList;

import com.ib.client.Contract;

/**
 * Stores a list of unique Contracts which are to be monitored with live stream of price data
 */
public class UniqueContractList {
    private final ArrayList <Contract> contractArrayList;

    public UniqueContractList() {
        contractArrayList = new ArrayList<>();
    }

    public void addContract(Contract contract){
        contractArrayList.add(contract);
    }

    public void removeContract(Contract contract) {
        contractArrayList.remove(contract);
    }

    public ArrayList<Contract> getContractArrayList() {
        return contractArrayList;
    }
}
