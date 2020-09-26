package org.example.contract;

import java.util.Optional;
import java.util.function.Function;

public class SingleArgLambdaImpersonator<ARG1> extends AbstractImpersonator<ARG1> {


    Function<ARG1, ARG1> preValidator;
    private FlatContract<ARG1> contract;


    public SingleArgLambdaImpersonator(FlatContract<ARG1> contract, Function<ARG1, ARG1> preValidator) {
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
