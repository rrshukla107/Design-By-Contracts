package org.rahul.dbc.engine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ContractExecutionEngineImpl implements ContractExecutionEngine {

    public static final double MILLI = 1000000d;
    private ExecutorService executorService;

    public ContractExecutionEngineImpl(final ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <ARG1> CompletableFuture<ContractExecutionResult> submitTask(final SingleArgContractWrapper<ARG1> contract) {

        CompletableFuture<ContractExecutionResult> result = new CompletableFuture<>();
        this.executorService.submit(() -> {
            try {
                long startTime = System.nanoTime();
                boolean contractResult = contract.getContract().validate(contract.getArgumentValue());
                long endTime = System.nanoTime();
                result.complete(new ContractExecutionResult(contractResult, (endTime - startTime) / MILLI));
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });

        return result;
    }

    @Override
    public <ARG1, ARG2> CompletableFuture<ContractExecutionResult> submitTask(BiContractWrapper<ARG1, ARG2> contract) {
        CompletableFuture<ContractExecutionResult> result = new CompletableFuture<>();
        this.executorService.submit(() -> {

            try {
                long startTime = System.nanoTime();
                boolean contractResult = contract.getFlatContract().validate(contract.getArg1(), contract.getArg2());
                long endTime = System.nanoTime();
                result.complete(new ContractExecutionResult(contractResult, (endTime - startTime) / MILLI));
            } catch (Throwable e) {
                result.completeExceptionally(e);
            }
        });

        return result;
    }

}
