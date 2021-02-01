package org.rahul.dbc.engine;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rahul.dbc.contract.chaperone.Chaperone;
import org.rahul.dbc.contract.chaperone.ChaperoneLambda;
import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.contract.flatcontract.SingleArgCachedFlatContract;
import org.rahul.dbc.contract.impersonator.Impersonator;
import org.rahul.dbc.contract.impersonator.ImpersonatorLambda;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContractChainExecutorImplTest {

    private ContractChainExecutor chainExecutor;

    private FlatContract<Object> contract1 = obj -> {
        try {
            System.out.println("Executing contract 1");
            TimeUnit.SECONDS.sleep(2);
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

        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Sample Exception Message");
    };


    private Impersonator<Object> impersonator1 = new ImpersonatorLambda<>(arg1 -> {
        System.out.println("In Impersonator");
        return arg1;
    }, contract1);

    private Chaperone<Object> chaperone1 = new ChaperoneLambda<>(arg1 -> {
        System.out.println("In chaperone1");
    }, contract2);

    private FlatContract<Object> cachedContract1 = new SingleArgCachedFlatContract<>(contract3);
    private FlatContract<Object> cachedContract2 = new SingleArgCachedFlatContract<>(impersonator1);
    private FlatContract<Object> cachedContract3 = new SingleArgCachedFlatContract<>(chaperone1);


    @Before
    public void setup() {
        this.chainExecutor = new ContractChainExecutorImpl(new ContractExecutionEngineImpl(Executors.newFixedThreadPool(5)));
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
    public void testChainWithContractHierarchy() throws ExecutionException, InterruptedException {

        CompletableFuture<ChainResult> promise = this.chainExecutor.executeChain(List.of(
                this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("impersonator1", impersonator1, new Object())
                , this.getWrapper("chaperone1", chaperone1, new Object())
                , this.getWrapper("cachedContract1", cachedContract1, new Object())
                , this.getWrapper("cachedContract2", cachedContract2, new Object())
                , this.getWrapper("cachedContract3", cachedContract3, new Object())));


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


    @Test
    public void runMultipleSuccessfulChainsParallel() {


        List<SingleArgContractWrapper<Object>> chain = List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contract2", contract2, new Object())
                , this.getWrapper("contract3", contract3, new Object()));


        //FORK
        CompletableFuture<ChainResult>[] promises = new CompletableFuture[]{
                this.chainExecutor.executeChain(chain),
                this.chainExecutor.executeChain(chain),
                this.chainExecutor.executeChain(chain)};

        try {
            // JOIN
            CompletableFuture.allOf(promises).get();

            Assert.assertTrue(promises[0].get().getResult());
            Assert.assertTrue(promises[1].get().getResult());
            Assert.assertTrue(promises[2].get().getResult());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void runMultipleChainsInParallelWithFailure() {

        List<SingleArgContractWrapper<Object>> successfulChain = List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contract2", contract2, new Object())
                , this.getWrapper("contract3", contract3, new Object()));

        List<SingleArgContractWrapper<Object>> chainWithFailure = List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contractFailure", contractFailure1, new Object())
                , this.getWrapper("contract3", contract3, new Object()));

        List<SingleArgContractWrapper<Object>> chainWithExceptions = List.of(this.getWrapper("contract1", contract1, new Object())
                , this.getWrapper("contractException", this.contractWithRuntimeError, new Object())
                , this.getWrapper("contract3", contract3, new Object()));


        //FORK
        CompletableFuture<ChainResult>[] promises = new CompletableFuture[]{
                this.chainExecutor.executeChain(successfulChain),
                this.chainExecutor.executeChain(chainWithFailure),
                this.chainExecutor.executeChain(chainWithExceptions)};

        try {
            // JOIN
            CompletableFuture.allOf(promises).get();

            //All contracts successful in this chain
            Assert.assertTrue(promises[0].get().getResult());

            //Contract Failure in this Chain
            Assert.assertFalse(promises[1].get().getResult());
            Assert.assertEquals(promises[1].get().getFailedContractName().get(), "contractFailure");

            //Failure due to Exception in this chain
            Assert.assertFalse(promises[2].get().getResult());
            Assert.assertEquals(promises[2].get().getFailedContractName().get(), "contractException");
            Assert.assertEquals(promises[2].get().getUnderlyingException().get().getMessage(), "Sample Exception Message");


        } catch (Exception e) {
            e.printStackTrace();
        }


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