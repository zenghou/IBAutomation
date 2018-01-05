package model;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;

/** Class that keeps track of the details in the callback function {@see EWrapperImplementation#openOrder} */
public class OpenOrderDetail {
    private int orderId;
    private Contract contract;

    public OpenOrderDetail(int orderId, Contract contract) {
        this.orderId = orderId;
        this.contract = contract;
    }

    /**
     * Returns the symbol of the contact in this OpenOrderDetail
     */
    public String getSymbol() throws Exception {
        if (contract == null) {
            throw new Exception("Contract object cannot be null!");
        } else {
            return contract.symbol();
        }
    }


    /**
     * Checks if an orderId matches this object's orderId
     */
    public boolean matchesOrderId(int orderId) {
        return (this.orderId == orderId);
    }
}
