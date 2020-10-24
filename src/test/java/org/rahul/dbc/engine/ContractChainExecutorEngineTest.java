package org.rahul.dbc.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rahul.dbc.contract.flatcontract.FlatContract;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContractChainExecutorEngineTest {

    private ContractChainExecutor chainExecutor;

    private FlatContract<Object> contract1 = obj -> {
        try {
            System.out.println("Executing contract 1");
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    };

    private FlatContract<Object> contract2 = obj -> {
        try {
            System.out.println("Executing contract 2");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    };

    private FlatContract<Object> contract3 = obj -> {
        try {
            System.out.println("Executing contract 3");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    };

    private FlatContract<Object> contractFailure1 = obj -> {
        try {
            System.out.println("Executing Failure 1");
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    };

    private FlatContract<Object> contractWithRuntimeError = obj -> {

        throw new RuntimeException("Sample Exception Message");
    };


    @Before
    public void setup() {
        this.chainExecutor = new ContractChainExecutorEngine(new ContractExecutionEngineImpl(Executors.newFixedThreadPool(5)));
    }

    @Test
    public void testSuccessfulChain() throws InterruptedException, ExecutionException {


        CompletableFuture<ChainResult> promise = this.chainExecutor.executeChain(List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contract2", contract2, new Object())
                , this.getWrapper("contract3", contract3, new Object())));


        ChainResult result = promise.get();
        Assert.assertTrue(result.getResult());

    }

    @Test
    public void testChainWithFailure() throws InterruptedException, ExecutionException {

        CompletableFuture<ChainResult> promise = this.chainExecutor.executeChain(List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contractFailure", contractFailure1, new Object())
                , this.getWrapper("contract3", contract3, new Object())));


        ChainResult chainResult = promise.get();

        Assert.assertFalse(chainResult.getResult());
        Assert.assertEquals(chainResult.getFailedContractName().get(), "contractFailure");

    }


    @Test
    public void testChainWithException() throws ExecutionException, InterruptedException {

        CompletableFuture<ChainResult> promise = this.chainExecutor.executeChain(List.of(this.getWrapper("contract1", contract1, new Object()),
                this.getWrapper("contractException", this.contractWithRuntimeError, new Object()),
                this.getWrapper("contract3", contract3, new Object())));

        ChainResult chainResult = promise.get();

        Assert.assertFalse(chainResult.getResult());
        Assert.assertEquals(chainResult.getFailedContractName().get(), "contractException");
        Assert.assertEquals(chainResult.getUnderlyingException().get().getMessage(), "Sample Exception Message");

    }

    private <ARG1> SingleArgContractWrapper<ARG1> getWrapper(final String name, final FlatContract<ARG1> contract, ARG1 object) {
        return new SingleArgContractWrapper<ARG1>(name, contract, object);
    }

}



/*        promise.thenAccept(result -> {
            try {
                Assert.assertFalse(result.getResult());
                Assert.assertEquals(result.getFailedContractName(), "contractFailure");
            } catch (Throwable e) {
                throw e;
            } finally {
                latch.countDown();
            }
        });

        latch.await();*/