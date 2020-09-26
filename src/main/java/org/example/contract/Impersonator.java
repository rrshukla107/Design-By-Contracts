package org.example.contract;

public interface Impersonator<ARG1> extends FlatContract<ARG1> {


    ARG1 preValidation(ARG1 data);

}
