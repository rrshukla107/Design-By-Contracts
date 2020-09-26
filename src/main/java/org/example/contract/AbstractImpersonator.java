package org.example.contract;

public abstract class AbstractImpersonator<T> implements Impersonator<T> {

    private FlatContract<T> contract;

    public AbstractImpersonator(FlatContract<T> contract) {
        this.contract = contract;
    }

    @Override
    public void validate(T data) {
        this.preValidation(data);

        this.contract.validate(data);

        this.postValidation(data);
    }
}
