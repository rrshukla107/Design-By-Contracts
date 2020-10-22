package org.rahul.dbc.engine;

import java.util.Optional;

public class ContractChainResult implements ChainResult {

    private boolean result;

    private Throwable underlyingException;

    public ContractChainResult(boolean result) {
        this.result = result;
    }

    public ContractChainResult(final Throwable exception) {
        this.underlyingException = exception;
        this.result = false;
    }

    @Override
    public boolean getResult() {
        return this.result;
    }

    @Override
    public boolean hasFailed() {
        return this.result == false;
    }

    @Override
    public boolean isSuccessful() {
        return this.result = true;
    }

    @Override
    public Optional<Throwable> getUnderlyingException() {
        return Optional.ofNullable(this.underlyingException);
    }
}
