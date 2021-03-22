package org.rahul.dbc.engine;

import java.util.concurrent.CompletableFuture;

public interface ContractExecutionEngine {
    <ARG1> CompletableFuture<ContractExecutionResult> submitTask(SingleArgContractWrapper<ARG1> contract);

    <ARG1, ARG2> CompletableFuture<ContractExecutionResult> submitTask(BiContractWrapper<ARG1, ARG2> contract);
}
