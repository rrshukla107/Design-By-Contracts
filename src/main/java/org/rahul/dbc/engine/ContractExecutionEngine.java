package org.rahul.dbc.engine;

import java.util.concurrent.CompletableFuture;

public interface ContractExecutionEngine {
    <ARG1> CompletableFuture<Boolean> submitTask(SingleArgContractWrapper<ARG1> contract);
}
