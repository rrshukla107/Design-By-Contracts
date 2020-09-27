package org.rahul.dbc.contract;

import java.util.Optional;
import java.util.function.Function;

public class SingleArgLambdaImpersonator<ARG1> extends AbstractImpersonator<ARG1> {


    Function<ARG1, ARG1> preValidator;
    private FlatContract<ARG1> contract;


    public SingleArgLambdaImpersonator(Function<ARG1, ARG1> preValidator, FlatContract<ARG1> contract) {
        super(contract);
        this.preValidator = preValidator;
    }


    @Override
    public ARG1 preValidation(ARG1 data) {
        return Optional.ofNullable(this.preValidator)
                .map(v -> v.apply(data))
                .orElse(data);

    }


}
