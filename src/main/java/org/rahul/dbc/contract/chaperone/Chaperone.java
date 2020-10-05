package org.rahul.dbc.contract.chaperone;

import org.rahul.dbc.contract.impersonator.Impersonator;

public interface Chaperone<ARG1> extends Impersonator<ARG1> {

    void chaperoneArgument(ARG1 data);
}
