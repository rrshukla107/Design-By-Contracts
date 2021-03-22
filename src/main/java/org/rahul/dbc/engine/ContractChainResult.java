package org.rahul.dbc.engine;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ContractChainResult implements ChainResult {

    private boolean result;

    private Throwable underlyingException;

    private final String failedContractName;

    private final Map<String, Double> executionTime;

    private ContractChainResult(boolean result, final String underlyingContract) {
        this.result = result;
        this.failedContractName = underlyingContract;
        this.executionTime = new HashMap<>();
    }

    private ContractChainResult(final Throwable exception, final String failedContractName) {
        this.underlyingException = exception;
        this.result = false;
        this.failedContractName = failedContractName;
        this.executionTime = new HashMap<>();
    }

    public static ContractChainResult successfulChainResult() {
        return new ContractChainResult(true, null);
    }

    public static ContractChainResult failedChainResult(final String failedContractName) {
        return new ContractChainResult(false, failedContractName);
    }

    public static ContractChainResult failedChainResultDueToException(final String failedContractName, Throwable exception) {
        return new ContractChainResult(exception, failedContractName);
    }

    @Override
    public boolean getResult() {
        return this.result;
    }

    @Override
    public boolean hasFailed() {
        return !this.result;
    }

    @Override
    public boolean isSuccessful() {
        return this.result == true;
    }

    @Override
    public Optional<Throwable> getUnderlyingException() {
        return Optional.ofNullable(this.underlyingException);
    }

    @Override
    public Optional<String> getFailedContractName() {
        return Optional.ofNullable(this.failedContractName);
    }

    @Override
    public Map<String, Double> getExecutionTimes() {
        return this.executionTime;
    }

    @Override
    public void setExecutionTimes(final Map<String, Double> result) {
        result.entrySet().stream().forEach(entry -> this.executionTime.put(entry.getKey(), entry.getValue()));
    }

}
