package org.example.contract;

@FunctionalInterface
public interface FlatContract<ARG1> {

    boolean validate(ARG1 data);
}
