package org.rahul.dbc.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ContractExecutionEngineImpl implements ContractExecutionEngine {

    private ExecutorService executorService;

    public ContractExecutionEngineImpl(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <ARG1> CompletableFuture<Boolean> submitTask(final SingleArgContractWrapper<ARG1> contract) {

        CompletableFuture<Boolean> result = new CompletableFuture<>();
        this.executorService.submit(() -> {
            boolean contractResult = contract.getContract().validate(contract.getArgumentValue());
            result.complete(contractResult);
        });

        return result;
    }

}
