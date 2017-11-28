package model.exceptions;

public class FullContractListException extends Exception{
    public FullContractListException() {
        super("ContractWithPriceDetail list is full! Unable to add more ContractWithPriceDetail objects");
    }
}
