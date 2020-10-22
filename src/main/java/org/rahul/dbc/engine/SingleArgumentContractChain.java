package org.rahul.dbc.engine;

import java.util.List;

public class SingleArgumentContractChain<ARG1> implements ContractChain {

    private List<SingleArgContractWrapper<ARG1>> contracts;

    public SingleArgumentContractChain(final List<SingleArgContractWrapper<ARG1>> contractMapping) {
        this.contracts = contractMapping;
    }

    @Override
    public ChainResult executeChain() {
        return null;
    }
}
