package org.rahul.dbc.contract.impersonator;

import org.apache.commons.lang3.tuple.Pair;
import org.rahul.dbc.contract.flatcontract.BiFlatContract;

public interface BiImpersonator<ARG1, ARG2> extends BiFlatContract<ARG1, ARG2> {

    Pair<ARG1, ARG2> impersonateArguments(ARG1 arg1, ARG2 arg2);

}
