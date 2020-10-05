package org.rahul.dbc.contract.flatcontract;

public class Or<ARG1> implements FlatContract<ARG1> {

    private FlatContract<ARG1> underlyingContract;
    private FlatContract<ARG1> wrapperContract;

    public Or(final FlatContract<ARG1> wrapperContract, final FlatContract<ARG1> underlyingContract) {
        this.underlyingContract = underlyingContract;
        this.wrapperContract = wrapperContract;
    }

    @Override
    public boolean validate(ARG1 data) {

        if (this.wrapperContract.validate(data)) {
            // SHORT-CIRCUIT
            return true;
        }
        return this.underlyingContract.validate(data);
    }
}

