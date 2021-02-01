package org.rahul.dbc.contract.chaperone;

import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.function.Consumer;

public class ChaperoneLambda<ARG1> extends AbstractChaperone<ARG1> {

    private Consumer<ARG1> chaperone;

    public ChaperoneLambda(Consumer<ARG1> chaperone, FlatContract<ARG1> contract) {
        super(contract);
        this.chaperone = chaperone;
    }

    @Override
    public void chaperoneArgument(ARG1 data) {
        this.chaperone.accept(data);
    }

}
