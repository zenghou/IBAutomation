package logic;

import com.ib.client.Contract;

import model.ContractWithPriceDetail;

public class ContractBuilder {
    /**
     * Creates a {@see ContractWithPriceDetail} object with a stock symbol
     * @param stockSymbol
     * @return a ContractWithPriceDetail object
     */
    public static ContractWithPriceDetail buildContractWithPriceDetail(String stockSymbol) {
        ContractWithPriceDetail contract = new ContractWithPriceDetail();
        contract.symbol(stockSymbol);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");
        return contract;
    }
}
