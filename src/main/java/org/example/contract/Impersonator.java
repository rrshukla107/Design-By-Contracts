package org.example.contract;

public interface Impersonator<T> extends FlatContract<T> {

    void preValidation(T data);

    void postValidation(T data);

}
