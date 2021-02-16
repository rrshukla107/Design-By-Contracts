package org.rahul.dbc.engine;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;

public class BiContractWrapper<ARG1, ARG2> {

    private String contractName;
    private BiFlatContract<ARG1, ARG2> flatContract;
    private ARG1 arg1;
    private ARG2 arg2;

    public BiContractWrapper(final String contractName, final BiFlatContract contract, final ARG1 arg1, final ARG2 arg2) {
        this.contractName = contractName;
        this.flatContract = contract;
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    public String getContractName() {
        return contractName;
    }

    public BiFlatContract<ARG1, ARG2> getFlatContract() {
        return flatContract;
    }

    public ARG1 getArg1() {
        return arg1;
    }

    public ARG2 getArg2() {
        return arg2;
    }
}
