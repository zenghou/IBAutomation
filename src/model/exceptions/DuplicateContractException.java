package model.exceptions;

public class DuplicateContractException extends Exception {
    public DuplicateContractException() {
        super("Contract already exists in the list!");
    }
}
