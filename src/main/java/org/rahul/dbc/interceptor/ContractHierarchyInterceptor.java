package org.rahul.dbc.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.contract.flatcontract.FlatContract;
import org.rahul.dbc.engine.ChainResult;
import org.rahul.dbc.engine.ContractChainExecutor;
import org.rahul.dbc.engine.SingleArgContractWrapper;
import org.rahul.dbc.validator.ValidatorFactory;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ContractHierarchyInterceptor implements MethodInterceptor {

    private final ValidatorFactory validatorFactory;

    private final ContractChainExecutor contractChainExecutor;

    @Inject
    public ContractHierarchyInterceptor(final ValidatorFactory validatorFactory, final ContractChainExecutor contractChainExecutor) {
        this.validatorFactory = validatorFactory;
        this.contractChainExecutor = contractChainExecutor;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> parameterMappings = InterceptorUtils.getParameterMappings(invocation, parameters);

        String[] invariants = invocation.getMethod().getAnnotation(Validate.class).value();

        Map<String, List<SingleArgContractWrapper<?>>> singleArgContracts = this.getSingleArgContracts(invariants, parameterMappings);
        Map<String, CompletableFuture<ChainResult>> resultMappings = executeSingleArgContracts(singleArgContracts);

        boolean hasFailed = hasContractFailed(resultMappings);
        //Mock code for reporting -- think about this
        String result = getContractChainDetails(resultMappings);

        if (hasFailed) {
            throw new RuntimeException(result);
        }
        return invocation.proceed();
    }

    private Map<String, CompletableFuture<ChainResult>> executeSingleArgContracts(Map<String, List<SingleArgContractWrapper<?>>> contracts) throws InterruptedException, ExecutionException {
        Map<String, CompletableFuture<ChainResult>> resultMappings = new HashMap<>();

        //Fork
        for (Map.Entry<String, List<SingleArgContractWrapper<?>>> entry : contracts.entrySet()) {
            List<SingleArgContractWrapper<Object>> typeCastedContracts = entry.getValue().stream().map(contract -> (SingleArgContractWrapper<Object>) contract).collect(Collectors.toList());
            resultMappings.put(entry.getKey(), this.contractChainExecutor.executeChain(typeCastedContracts));
        }

        //Join
        CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();
        return resultMappings;
    }

    private String getContractChainDetails(Map<String, CompletableFuture<ChainResult>> resultMappings) throws InterruptedException, ExecutionException {
        StringBuilder result = new StringBuilder();

        for (Map.Entry<String, CompletableFuture<ChainResult>> entry : resultMappings.entrySet()) {

            ChainResult chainResult = entry.getValue().get();
            if (chainResult.isSuccessful()) {

                result.append("CHAIN - " + entry.getKey() + " PASSED SUCCESSFULLY\n");
            } else {
                result.append("CHAIN - " + entry.getKey() + " FAILED\n");
                result.append("FAILED CONTRACT NAME - " + chainResult.getFailedContractName().get());
                chainResult.getUnderlyingException()
                        .map(ExceptionUtils::getStackTrace)
                        .ifPresent(result::append);
            }

        }
        return result.toString();
    }

    private boolean hasContractFailed(Map<String, CompletableFuture<ChainResult>> resultMappings) {
        boolean hasFailed = resultMappings.values().stream().map(promise -> {
            try {
                return promise.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }).map(ChainResult::isSuccessful).reduce(true, (a, b) -> a && b);
        return !hasFailed;
    }


    private SingleArgContractWrapper getSingleArgContractWrapper(final String contractName, final FlatContract<?> contract, final Object argument) {

        return new SingleArgContractWrapper(contractName, contract, argument);
    }

    private Map<String, List<SingleArgContractWrapper<?>>> getSingleArgContracts(String[] invariants, Map<String, Object> parameterMappings) {

        Map<String, List<SingleArgContractWrapper<?>>> invariantChainMappings = new HashMap<>();

        for (String s : invariants) {
            String[] chainDefinition = s.split("=");
            String key = chainDefinition[0].strip();

            String[] validatorNames = chainDefinition[1].strip().split(",");

            List<FlatContract<?>> contracts = new ArrayList<>();
            List<SingleArgContractWrapper<?>> contractWrappers = new ArrayList<>();

            for (String validator : validatorNames) {
                this.validatorFactory.getValidator(validator.strip())
                        .map(v -> new SingleArgContractWrapper(validator, v, parameterMappings.get(key))).ifPresent(contractWrappers::add);

            }

            invariantChainMappings.put(key, contractWrappers);
        }

        return invariantChainMappings;

    }


}
