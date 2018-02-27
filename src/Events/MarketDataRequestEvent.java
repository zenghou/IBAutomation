package Events;

import model.UniqueContractList;

public class MarketDataRequestEvent extends BaseEvent {

    public final int numberOfStocks;
    public final UniqueContractList requestedContractList;

    public MarketDataRequestEvent(int numberOfStocks, UniqueContractList requestedContractList ) {
        this.numberOfStocks = numberOfStocks;
        this.requestedContractList = requestedContractList;
    }

    public int getNumberOfStocks() {
        return numberOfStocks;
    }

    public UniqueContractList getRequestedContractList() {
        return requestedContractList;
    }

    @Override
    public String toString() {
        return "Number of data requests sent: " + numberOfStocks;
    }
}
