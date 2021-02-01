package org.rahul.dbc.contract.chaperone;

import org.rahul.dbc.contract.flatcontract.BiFlatContract;

import java.util.function.BiConsumer;

public class BiChaperoneLambda<ARG1, ARG2> extends AbstractBiChaperone<ARG1, ARG2> {


    private BiConsumer<ARG1, ARG2> chaperoneTask;

    public BiChaperoneLambda(final BiFlatContract<ARG1, ARG2> flatContract, final BiConsumer<ARG1, ARG2> chaperoneTask) {
        super(flatContract);
        this.chaperoneTask = chaperoneTask;
    }

    @Override
    public void chaperoneArgument(ARG1 arg1, ARG2 arg2) {
        this.chaperoneTask.accept(arg1, arg2);
    }
}
