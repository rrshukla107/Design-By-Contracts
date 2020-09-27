package org.rahul.dbc.contract;

@FunctionalInterface
public interface FlatContract<ARG1> {

    boolean validate(ARG1 data);
}
