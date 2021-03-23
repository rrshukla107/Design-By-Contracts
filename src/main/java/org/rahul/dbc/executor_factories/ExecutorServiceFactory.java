package org.rahul.dbc.executor_factories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceFactory {

    private static ExecutorService FIXED_THREAD_POOL = Executors.newFixedThreadPool(6);


    public static ExecutorService getFixedThreadPoolExecutorService() {
        return FIXED_THREAD_POOL;
    }

    public static void shutDownExecutorService() throws InterruptedException {
        FIXED_THREAD_POOL.shutdown();
        FIXED_THREAD_POOL.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

}
