package org.rahul.dbc.contract.impersonator;

import org.rahul.dbc.contract.flatcontract.FlatContract;

public interface Impersonator<ARG1> extends FlatContract<ARG1> {


    ARG1 impersonateArgument(ARG1 data);

}
