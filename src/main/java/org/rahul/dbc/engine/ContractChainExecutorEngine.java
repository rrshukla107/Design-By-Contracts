package org.rahul.dbc.engine;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ContractChainExecutorEngine implements ContractChainExecutor {

    private final ContractExecutionEngine contractExecutionEngine;


    public ContractChainExecutorEngine(final ContractExecutionEngine contractExecutionEngine) {
        this.contractExecutionEngine = contractExecutionEngine;
    }

    @Override
    public <ARG1> CompletableFuture<ChainResult> executeChain(final List<SingleArgContractWrapper<ARG1>> contractChain) {

        CompletableFuture<ChainResult> result = new CompletableFuture<>();
        AtomicInteger i = new AtomicInteger(0);

        this.executeTask(result, i, contractChain);

        return result;
    }

    private <ARG1> void executeTask(CompletableFuture<ChainResult> result, AtomicInteger index, final List<SingleArgContractWrapper<ARG1>> contractChain) {


        this.contractExecutionEngine.submitTask(contractChain.get(index.getAndIncrement())).whenComplete((isSuccessful, failure) -> {

            if (failure != null) {
                result.complete(
                        ContractChainResult.failedChainResultDueToException(
                                contractChain.get(index.get() - 1).getContractName(), failure));
            } else if (!isSuccessful) {
                result.complete(
                        ContractChainResult.failedChainResult(contractChain.get(index.get() - 1).getContractName()));
            } else if (index.get() < contractChain.size()) {
                this.executeTask(result, index, contractChain);

            } else if (index.get() == contractChain.size()) {
                result.complete(ContractChainResult.successfulChainResult());
            }

        });

    }
}
