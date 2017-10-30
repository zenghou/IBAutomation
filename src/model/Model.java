package model;

import java.util.ArrayList;

import com.ib.client.Contract;

/**
 * The API of the model component
 */
public interface Model {
    /** Returns a UniqueContractList object */
    UniqueContractList getUniqueContractList();

    /** Returns a list of iterable Contracts from a UniqueContractList object */
    ArrayList<Contract> getViewOnlyContractList();

    /** Sets up a model with a {@see listOfSymbols} and {@see UniqueContractList} */
    void initializeModel();

    /** Returns an String arraylist for the {@see Parser} to populate with symbols */
    ArrayList<String> getListOfSymbolsArray();
}
