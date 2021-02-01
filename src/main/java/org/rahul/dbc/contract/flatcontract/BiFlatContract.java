package org.rahul.dbc.contract.flatcontract;

@FunctionalInterface
public interface BiFlatContract<ARG1, ARG2> {

    boolean validate(ARG1 data1, ARG2 data2);

    default BiFlatContract<ARG1, ARG2> and(BiFlatContract<ARG1, ARG2> contract) {
        return (arg1, arg2) -> this.validate(arg1, arg2) && contract.validate(arg1, arg2);
    }

    default BiFlatContract<ARG1, ARG2> or(BiFlatContract<ARG1, ARG2> contract) {
        return (arg1, arg2) -> this.validate(arg1, arg2) || contract.validate(arg1, arg2);
    }

}
