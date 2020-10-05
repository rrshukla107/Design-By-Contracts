package org.rahul.dbc.contract;

public interface Impersonator<ARG1> extends FlatContract<ARG1> {


    ARG1 impersonateArgument(ARG1 data);

}
