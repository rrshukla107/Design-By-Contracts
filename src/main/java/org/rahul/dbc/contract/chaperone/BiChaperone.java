package org.rahul.dbc.contract.chaperone;

import org.rahul.dbc.contract.impersonator.BiImpersonator;

public interface BiChaperone<ARG1, ARG2> extends BiImpersonator<ARG1, ARG2> {

    void chaperoneArgument(ARG1 arg1, ARG2 arg2);

}
