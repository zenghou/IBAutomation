//@@author zenghou
package model;

import com.ib.client.Contract;

import model.ContractWithPriceDetail;

public class ContractBuilder {
    public static final double MINIMUM_PERCENTAGE_DECREASE = 6.00;

    /**
     * Creates a {@see ContractWithPriceDetail} object with a stock symbol and its opening price
     * @param ticker
     * @return a ContractWithPriceDetail object
     */
    public static ContractWithPriceDetail buildContractWithPriceDetail(String ticker, double openingPrice) {
        ContractWithPriceDetail contract = new ContractWithPriceDetail(openingPrice,
                MINIMUM_PERCENTAGE_DECREASE);

        contract.symbol(ticker);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");

        return contract;
    }

    /**
     * Creates a {@see ContractWithPriceDetail} object from a {@see Contract}
     * @return a ContractWithPriceDetail object
     */
    public static ContractWithPriceDetail buildContractWithPriceDetailFromContract(Contract contract) {
        ContractWithPriceDetail contractWithPriceDetail = new ContractWithPriceDetail();

        // populate with information from Contract
        contractWithPriceDetail.symbol(contract.symbol());
        contractWithPriceDetail.secType("STK");
        contractWithPriceDetail.currency("USD");
        contractWithPriceDetail.exchange("SMART");
        contractWithPriceDetail.primaryExch("ISLAND");

        return contractWithPriceDetail;
    }

    public static Contract buildContractWithSymbol(String symbol) {
        Contract contract = new Contract();

        contract.symbol(symbol);
        contract.secType("STK");
        contract.currency("USD");
        contract.exchange("SMART");
        contract.primaryExch("ISLAND");

        return contract;
    }
}
