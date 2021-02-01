package org.rahul.dbc.contract.chaperone;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rahul.dbc.contract.flatcontract.BiFlatContract;
import org.rahul.dbc.contract.impersonator.AbstractBiImpersonator;

public abstract class AbstractBiChaperone<ARG1, ARG2> extends AbstractBiImpersonator<ARG1, ARG2> implements BiChaperone<ARG1, ARG2> {


    public AbstractBiChaperone(final BiFlatContract<ARG1, ARG2> flatContract) {
        super(flatContract);
    }


    @Override
    public Pair<ARG1, ARG2> impersonateArguments(ARG1 arg1, ARG2 arg2) {

        this.chaperoneArgument(arg1, arg2);
        // No changes to the arguments
        return new ImmutablePair<>(arg1, arg2);
    }
}
