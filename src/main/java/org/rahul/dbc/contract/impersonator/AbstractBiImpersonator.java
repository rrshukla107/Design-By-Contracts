package org.rahul.dbc.contract.impersonator;

import org.apache.commons.lang3.tuple.Pair;
import org.rahul.dbc.contract.flatcontract.BiFlatContract;

public abstract class AbstractBiImpersonator<ARG1, ARG2> implements BiImpersonator<ARG1, ARG2> {

    protected BiFlatContract<ARG1, ARG2> flatContract;

    public AbstractBiImpersonator(BiFlatContract<ARG1, ARG2> flatContract) {
        this.flatContract = flatContract;
    }


    @Override
    public boolean validate(ARG1 data1, ARG2 data2) {

        Pair<ARG1, ARG2> pair = this.impersonateArguments(data1, data2);
        return this.flatContract.validate(pair.getLeft(), pair.getRight());
    }
}
