package Events;

import model.UniqueContractList;

public class MarketDataRequestCompleteEvent extends BaseEvent {
    public final UniqueContractList requestedContractList;

    public MarketDataRequestCompleteEvent(UniqueContractList requestedContractList ) {
        this.requestedContractList = requestedContractList;
    }

    public UniqueContractList getRequestedContractList() {
        return requestedContractList;
    }

    @Override
    public String toString() {
        return "Real time market data request completed for this contract list";
    }
}
