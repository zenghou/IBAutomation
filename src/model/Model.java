//@@author zenghou
package model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * The API of the model component
 */
public interface Model {
    /** Returns a UniqueContractList object */
    UniqueContractList getUniqueContractList();

    /** Returns a list of iterable Contracts from a UniqueContractList object */
    ArrayList<ContractWithPriceDetail> getViewOnlyContractWithPriceDetailList();

    /** Sets up a model with a {@see listOfSymbols} and {@see UniqueContractList} */
    void initializeModel();

    /** Returns an String Double Hash Map for the {@see Parser} to populate with ticker and prices */
    HashMap<String, Double> getTickerPriceHashMap();

    /** Retrieves a ContractWithPriceDetails by requestId and returns it to caller*/
    ContractWithPriceDetail retrieveContractWithPriceDetailByReqId(int reqId);

    /** Returns a UniqueOrderContractList object */
    UniqueOrderContractList getUniqueOrderContractList();

    /** Adds a contract that is ready for purchase order to the UniqueOrderContractList */
    void addContractWithPriceDetailToOrderList(ContractWithPriceDetail contractWithPriceDetail);

    void updateUniqueContractList(ContractWithPriceDetail contract);

    UniqueContractList getUniqueContractToCloseList();
}
