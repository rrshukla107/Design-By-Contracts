package org.example.contract;

public interface FlatContract<T> {

    void validate(T data);
}
