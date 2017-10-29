package logic;

import com.ib.client.Contract;

public class ContractBuilder {
    /**
     * Creates a Contract object with a stock symbol
     * @param stockSymbol
     * @return a Contract object
     */
    public static Contract buildStock(String stockSymbol) {
        Contract contract = new Contract();
        contract.symbol(stockSymbol);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");
        return contract;
    }
}
