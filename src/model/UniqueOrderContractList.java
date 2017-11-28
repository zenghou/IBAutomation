package model;

/**
 * Stores a list of unique ContractWithPriceDetails which should be submitted for limit buy order
 */
public class UniqueOrderContractList extends UniqueContractList {
    // use superclass's default constructor
    public UniqueOrderContractList() {
        super();
    }

    public UniqueOrderContractList(int maxNumberOfContracts) {
        super(maxNumberOfContracts);
    }
}
