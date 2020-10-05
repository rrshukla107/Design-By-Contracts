package org.rahul.dbc.contract.impersonator;

import org.rahul.dbc.contract.flatcontract.FlatContract;

public abstract class AbstractImpersonator<ARG1> implements Impersonator<ARG1> {

    private FlatContract<ARG1> contract;


    public AbstractImpersonator(final FlatContract<ARG1> contract) {
        this.contract = contract;
    }


    @Override
    public boolean validate(ARG1 data) {
        return this.contract.validate(this.impersonateArgument(data));
    }
}
