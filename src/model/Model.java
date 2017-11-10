package model;

import java.util.ArrayList;

import com.ib.client.Contract;

/**
 * The API of the model component
 */
public interface Model {
    /** Returns a UniqueContractWithPriceDetailList object */
    UniqueContractWithPriceDetailList getUniqueContractWithPriceDetailList();

    /** Returns a list of iterable Contracts from a UniqueContractWithPriceDetailList object */
    ArrayList<ContractWithPriceDetail> getViewOnlyContractWithPriceDetailList();

    /** Sets up a model with a {@see listOfSymbols} and {@see UniqueContractWithPriceDetailList} */
    void initializeModel();

    /** Returns an String arraylist for the {@see Parser} to populate with symbols */
    ArrayList<String> getListOfSymbolsArray();

    /** Retrieves a ContractWithPriceDetails by requestId and returns it to caller*/
    ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId);
}
