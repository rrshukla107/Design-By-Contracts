package org.rahul.dbc.engine;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Executors;

public class ContractChainExecutorEngineTest {

    private ContractChainExecutor chainExecutor;

    @Before
    public void setup() {
        this.chainExecutor = new ContractChainExecutorEngine(new ContractExecutionEngineImpl(Executors.newFixedThreadPool(5)));
    }

    @Test
    public void testOneChain() {

    }

}