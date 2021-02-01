package org.rahul.dbc.contract.impersonator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rahul.dbc.contract.flatcontract.BiFlatContract;

import java.util.Optional;
import java.util.function.BiFunction;

public class BiImpersonatorLambda<ARG1, ARG2> extends AbstractBiImpersonator<ARG1, ARG2> {

    private final BiFunction<ARG1, ARG2, Pair<ARG1, ARG2>> impersonator;

    public BiImpersonatorLambda(final BiFlatContract<ARG1, ARG2> contract, final BiFunction<ARG1, ARG2, Pair<ARG1, ARG2>> impersonator) {
        super(contract);
        this.impersonator = impersonator;
    }

    @Override
    public Pair<ARG1, ARG2> impersonateArguments(ARG1 arg1, ARG2 arg2) {

        return Optional.ofNullable(this.impersonator)
                .map(i -> i.apply(arg1, arg2))
                .orElse(new ImmutablePair<>(arg1, arg2));
    }
}
