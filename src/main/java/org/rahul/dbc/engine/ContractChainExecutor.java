package org.rahul.dbc.engine;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ContractChainExecutor {

    <ARG1> CompletableFuture<ChainResult> executeContractChain(final List<SingleArgContractWrapper<ARG1>> contractChain);

    <ARG1, ARG2> CompletableFuture<ChainResult> executeBiContractChain(final List<BiContractWrapper<ARG1, ARG2>> contractChain);
}
