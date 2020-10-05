package org.rahul.dbc.contract.flatcontract;

public class And<ARG1> implements FlatContract<ARG1> {

    private FlatContract<ARG1> underlyingContract;
    private FlatContract<ARG1> wrapperContract;

    public And(final FlatContract<ARG1> wrapperContract, final FlatContract<ARG1> underlyingContract) {
        this.underlyingContract = underlyingContract;
        this.wrapperContract = wrapperContract;
    }

    @Override
    public boolean validate(ARG1 data) {

        if (this.wrapperContract.validate(data)) {
            return this.underlyingContract.validate(data);
        }
        // SHORT-CIRCUIT
        return false;
    }
}
