package org.rahul.dbc.executor_factories;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceFactory {

    private static ExecutorService FIXED_THREAD_POOL_SIZE_3 = Executors.newFixedThreadPool(3);


    public static ExecutorService getFixedThreadPoolExecutorService() {
        return FIXED_THREAD_POOL_SIZE_3;
    }

    public static void shutDownExecutorService() throws InterruptedException {
        FIXED_THREAD_POOL_SIZE_3.shutdown();
        FIXED_THREAD_POOL_SIZE_3.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
    }

}
