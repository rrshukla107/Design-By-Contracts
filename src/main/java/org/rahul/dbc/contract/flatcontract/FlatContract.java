package org.rahul.dbc.contract.flatcontract;

@FunctionalInterface
public interface FlatContract<ARG1> {

    boolean validate(ARG1 data);

    default FlatContract<ARG1> and(FlatContract<ARG1> contract) {
        return arg -> validate(arg) && contract.validate(arg);
    }

    default FlatContract<ARG1> or(FlatContract<ARG1> contract) {
        return arg -> validate(arg) || contract.validate(arg);
    }
}
