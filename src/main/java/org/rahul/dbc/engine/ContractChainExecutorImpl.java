package org.rahul.dbc.engine;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ContractChainExecutorImpl implements ContractChainExecutor {

    private final ContractExecutionEngine contractExecutionEngine;


    public ContractChainExecutorImpl(final ContractExecutionEngine contractExecutionEngine) {
        this.contractExecutionEngine = contractExecutionEngine;
    }

    @Override
    public <ARG1> CompletableFuture<ChainResult> executeContractChain(final List<SingleArgContractWrapper<ARG1>> contractChain) {

        CompletableFuture<ChainResult> result = new CompletableFuture<>();
        AtomicInteger i = new AtomicInteger(0);
        Map<String, Double> executionTimeAccumulator = new HashMap<>();

        this.executeTask(result, i, contractChain, executionTimeAccumulator);

        return result;
    }

    @Override
    public <ARG1, ARG2> CompletableFuture<ChainResult> executeBiContractChain(List<BiContractWrapper<ARG1, ARG2>> contractChain) {
        CompletableFuture<ChainResult> result = new CompletableFuture<>();
        AtomicInteger i = new AtomicInteger(0);

        Map<String, Double> executionTimeAccumulator = new HashMap<>();

        this.executeBiTask(result, i, contractChain, executionTimeAccumulator);

        return result;
    }

    private <ARG1> void executeTask(CompletableFuture<ChainResult> result, AtomicInteger index, final List<SingleArgContractWrapper<ARG1>> contractChain, Map<String, Double> accumulator) {


        this.contractExecutionEngine.submitTask(contractChain.get(index.getAndIncrement())).whenComplete((contractExecutionResult, failure) -> {

            String contractName = contractChain.get(index.get() - 1).getContractName();
            if (failure != null) {

                ContractChainResult contractChainResult = ContractChainResult.failedChainResultDueToException(
                        contractName, failure);
                // We cannot time the execution time for contracts which have failed due to exception
                accumulator.put(contractName, 0d);
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(
                        contractChainResult);

            } else if (!contractExecutionResult.getResult()) {
                ContractChainResult contractChainResult = ContractChainResult.failedChainResult(contractName);
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(contractChainResult);

            } else if (index.get() < contractChain.size()) {
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                this.executeTask(result, index, contractChain, accumulator);

            } else if (index.get() == contractChain.size()) {
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                ContractChainResult contractChainResult = ContractChainResult.successfulChainResult();
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(contractChainResult);

            }

        });

    }

    private <ARG1, ARG2> void executeBiTask(CompletableFuture<ChainResult> result, AtomicInteger index, final List<BiContractWrapper<ARG1, ARG2>> contractChain, Map<String, Double> accumulator) {


        this.contractExecutionEngine.submitTask(contractChain.get(index.getAndIncrement())).whenComplete((contractExecutionResult, failure) -> {
            String contractName = contractChain.get(index.get() - 1).getContractName();

            if (failure != null) {
                // We cannot time the execution time for contracts which have failed due to exception
                accumulator.put(contractName, 0d);
                ContractChainResult contractChainResult = ContractChainResult.failedChainResultDueToException(
                        contractName, failure);
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(contractChainResult);

            } else if (!contractExecutionResult.getResult()) {
                ContractChainResult contractChainResult = ContractChainResult.failedChainResult(contractName);
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(
                        contractChainResult);

            } else if (index.get() < contractChain.size()) {
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                this.executeBiTask(result, index, contractChain, accumulator);

            } else if (index.get() == contractChain.size()) {
                accumulator.put(contractName, contractExecutionResult.getRunTime());
                ContractChainResult contractChainResult = ContractChainResult.successfulChainResult();
                contractChainResult.setExecutionTimes(accumulator);
                result.complete(contractChainResult);
            }

        });

    }
}
