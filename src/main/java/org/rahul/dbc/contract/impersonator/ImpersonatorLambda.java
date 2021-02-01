package org.rahul.dbc.contract.impersonator;

import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.Optional;
import java.util.function.Function;

public class ImpersonatorLambda<ARG1> extends AbstractImpersonator<ARG1> {


    private Function<ARG1, ARG1> impersonator;


    public ImpersonatorLambda(Function<ARG1, ARG1> impersonator, FlatContract<ARG1> contract) {
        super(contract);
        this.impersonator = impersonator;
    }


    @Override
    public ARG1 impersonateArgument(ARG1 data) {
        return Optional.ofNullable(this.impersonator)
                .map(v -> v.apply(data))
                .orElse(data);

    }


}
