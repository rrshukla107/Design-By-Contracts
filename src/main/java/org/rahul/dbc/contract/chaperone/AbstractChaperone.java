package org.rahul.dbc.contract.chaperone;

import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.impersonator.AbstractImpersonator;

public abstract class AbstractChaperone<ARG1> extends AbstractImpersonator<ARG1> implements Chaperone<ARG1> {


    public AbstractChaperone(final FlatContract<ARG1> contract) {
        super(contract);
    }

    @Override
    public ARG1 impersonateArgument(ARG1 data) {
        this.chaperoneArgument(data);
        // We are not allowing modifications to the input data
        return data;
    }
}
