package org.rahul.dbc.engine;

import org.rahul.dbc.contract.flatcontract.FlatContract;

public class SingleArgContractWrapper<ARG1> {

    private String contractName;
    private FlatContract<ARG1> contract;
    private ARG1 argumentValue;

    private SingleArgContractWrapper(final String contractName, final FlatContract<ARG1> contract, final ARG1 argumentValue) {
        this.contractName = contractName;
        this.contract = contract;
        this.argumentValue = argumentValue;
    }

    public String getContractName() {
        return contractName;
    }

    public FlatContract<ARG1> getContract() {
        return contract;
    }

    public ARG1 getArgumentValue() {
        return this.argumentValue;
    }
}
