package org.rahul.dbc.contract.flatcontract;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SingleArgCachedFlatContract<ARG1> implements FlatContract<ARG1> {

    private FlatContract<ARG1> underlyingContract;
    private Map<ARG1, Boolean> cache;
    private Lock lock;

    public SingleArgCachedFlatContract(final FlatContract<ARG1> underlyingContract) {
        this.underlyingContract = underlyingContract;
        this.lock = new ReentrantLock();
        this.cache = new HashMap<>();
    }

    @Override
    public boolean validate(ARG1 data) {

        if (this.cache.containsKey(data)) {
            return this.cache.get(data);
        }

        boolean result = this.underlyingContract.validate(data);
        this.lock.lock();
        try {
            this.cache.put(data, result);
        } finally {
            this.lock.unlock();
        }

        return result;
    }
}
