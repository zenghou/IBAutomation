//@@author zenghou
package model;

import java.util.ArrayList;

import com.ib.client.EWrapper;

import logic.EWrapperImplementation;
import logic.Logic;
import model.exceptions.DuplicateContractException;
import model.exceptions.FullContractListException;

/**
 * Stores a list of unique ContractWithPriceDetails which should be submitted for limit buy order
 */
public class UniqueOrderContractList extends UniqueContractList {
    private Logic logic = null;

    // use superclass's default constructor
    public UniqueOrderContractList() {
        super();
    }

    public UniqueOrderContractList(int maxNumberOfContracts) {
        super(maxNumberOfContracts);
    }

    public void setLogic(Logic logic) {
        this.logic = logic;
    }

    /**
     * Adds {@code ContractWithPriceDetail} to UniqueOrderContractList and notifies logic to submit order
     * * Could consider using Google's event bus/Observer & Subscriber model to reduce coupling
     *
     */
    @Override
    public void
    addContract(ContractWithPriceDetail contract) throws FullContractListException,
            DuplicateContractException {
        super.addContract(contract);

        // set current OrderId to contract
        try {
            contract.setOrderId(EWrapperImplementation.getCurrentOrderId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // notify Logic to send order
        newContractAddedNotifyLogicToSendOrder(contract);

        // notify Logic to stop requesting real time bars for contract only if it is from model's uniqueContractList
        cancelRealTimeMarketDataRequestForContract(contract);
    }

    /**
     * Asks Logic to send order when ContractWithPriceDetail is added to UniqueContractOrderList
     * @param contract
     */
    private void newContractAddedNotifyLogicToSendOrder (ContractWithPriceDetail contract) {
        // logic must be set up
        assert(logic != null);
        // TODO: change buy order parameters
        logic.placeLimitBuyOrder(contract, 90, 20);
    }

    /**
     * Takes in a {@code contract} and terminates the realtimebar request, which is no longer needed since a buy order
     * must have been placed.
     */
    private void cancelRealTimeBarRequestForContract(ContractWithPriceDetail contract) {
        logic.cancelRealTimeBarsForContract(contract);
    }

    /**
     * Takes in a {@code contract} and terminates reqMktData, which is no longer needed since a buy order
     * must have been placed.
     */
    private void cancelRealTimeMarketDataRequestForContract(ContractWithPriceDetail contract) {
        logic.cancelRealTImeMarketDataForContract(contract);
    }

    public void printAll() {
        ArrayList<ContractWithPriceDetail> array = getContractArrayWithPriceDetailList();
        System.out.println("currently in ORDER LIST");
        for (ContractWithPriceDetail contract: array) {
            System.out.println(contract.symbol());
        }
    }

    /**
     * Checks if the orderId is an orderId belonging to a ContractWithPriceDetail in this order list.
     * @param orderId
     */
    public boolean containsOrderId(int orderId) {
        for (ContractWithPriceDetail contract : getContractArrayWithPriceDetailList()) {
            if (contract.getOrderId() == orderId) {
                return true;
            }
        }
        return false;
    }

    public String retrieveSymbolByOrderId(int orderId) {
        for (ContractWithPriceDetail contract : getContractArrayWithPriceDetailList()) {
            if (contract.getOrderId() == orderId) {
                return contract.symbol();
            }
        }
        return "Unable to retrieve symbol for orderId: " + orderId;
    }
}
