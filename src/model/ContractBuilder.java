//@@author zenghou
package model;

import com.ib.client.Contract;

import model.ContractWithPriceDetail;

public class ContractBuilder {
    public static final double MINIMUM_PERCENTAGE_DECREASE = 13.00;

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
        contractWithPriceDetail.secType(contract.secType());
        contractWithPriceDetail.currency(contract.currency());
        contractWithPriceDetail.exchange(contract.exchange());
        contractWithPriceDetail.primaryExch(contract.primaryExch());

        return contractWithPriceDetail;
    }
}
