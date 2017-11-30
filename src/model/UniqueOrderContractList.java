//@@author zenghou
package model;

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
    public void addContract(ContractWithPriceDetail contract) throws FullContractListException,
            DuplicateContractException {
        super.addContract(contract);
        newContractAddedNotifyLogicToSendOrder(contract);
    }

    /**
     * Asks Logic to send order when ContractWithPriceDetail is added to UniqueContractOrderList
     * @param contract
     */
    private void newContractAddedNotifyLogicToSendOrder (ContractWithPriceDetail contract) {
        // logic must be set up
        assert(logic != null);

        logic.placeLimitBuyOrder(contract);
    }
}
