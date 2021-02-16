package org.rahul.dbc.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rahul.dbc.annotations.BiValidate;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.engine.BiContractWrapper;
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

        // check whether annotations exist or not??
        String[] invariants = invocation.getMethod().getAnnotation(Validate.class).value();
        Map<String, List<SingleArgContractWrapper<?>>> singleArgContracts = this.getSingleArgContracts(invariants, parameterMappings);
        Map<String, CompletableFuture<ChainResult>> resultMappings = executeSingleArgContracts(singleArgContracts);

        String[] biInvariants = invocation.getMethod().getAnnotation(BiValidate.class).value();
        Map<String, List<BiContractWrapper<?, ?>>> biContracts = this.getBiArgContractWrapper(biInvariants, parameterMappings);
        resultMappings.putAll(this.executeBiContracts(biContracts));

        //Join
        CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();

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
            List<SingleArgContractWrapper<Object>> typeCastedContracts = entry.getValue().stream()
                    .map(contract -> (SingleArgContractWrapper<Object>) contract)
                    .collect(Collectors.toList());
            resultMappings.put(entry.getKey(), this.contractChainExecutor.executeChain(typeCastedContracts));
        }

//Join
//        CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();
        return resultMappings;
    }

    private Map<String, CompletableFuture<ChainResult>> executeBiContracts(Map<String, List<BiContractWrapper<?, ?>>> contracts) {
        //Fork
        Map<String, CompletableFuture<ChainResult>> chainResultPromiseMappings = new HashMap<>();
        for (Map.Entry<String, List<BiContractWrapper<?, ?>>> entry : contracts.entrySet()) {
            List<BiContractWrapper<Object, Object>> typeCastedContracts = entry.getValue().stream()
                    .map(contract -> (BiContractWrapper<Object, Object>) contract)
                    .collect(Collectors.toList());

            chainResultPromiseMappings.put(entry.getKey(), this.contractChainExecutor.executeBiContractChain(typeCastedContracts));
        }

        return chainResultPromiseMappings;
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

    private Map<String, List<SingleArgContractWrapper<?>>> getSingleArgContracts(String[] invariants, Map<String, Object> parameterMappings) {

        Map<String, List<SingleArgContractWrapper<?>>> invariantChainMappings = new HashMap<>();

        for (String s : invariants) {
            String[] chainDefinition = s.split("=");
            String key = chainDefinition[0].strip();

            String[] validatorNames = chainDefinition[1].strip().split(",");

            List<SingleArgContractWrapper<?>> contractWrappers = new ArrayList<>();

            for (String validator : validatorNames) {
                this.validatorFactory.getValidator(validator.strip())
                        .map(v -> new SingleArgContractWrapper(validator, v, parameterMappings.get(key))).ifPresent(contractWrappers::add);

            }

            invariantChainMappings.put(key, contractWrappers);
        }

        return invariantChainMappings;

    }


    private Map<String, List<BiContractWrapper<?, ?>>> getBiArgContractWrapper(String[] invariants, Map<String, Object> parameterMappings) {

        Map<String, List<BiContractWrapper<?, ?>>> contractChainMappings = new HashMap<>();

        for (String invariant : invariants) {

            String[] chainDefinition = invariant.split("=");
            String chainName = chainDefinition[0].strip();

            String[] contractDetails = chainDefinition[1].strip().split("->");
            List<BiContractWrapper<?, ?>> contractWrappers = new ArrayList<>();

            for (String contract : contractDetails) {

                String strippedContract = contract.strip();
                String contractName = strippedContract.substring(0, strippedContract.indexOf("(")).strip();
                String[] args = strippedContract
                        .substring(strippedContract.indexOf("(") + 1, strippedContract.indexOf(")"))
                        .strip().split(",");


                this.validatorFactory.getBiValidator(contractName)
                        .map(v -> new BiContractWrapper(contractName, v, parameterMappings.get(args[0].strip()), parameterMappings.get(args[1].strip())))
                        .ifPresent(contractWrappers::add);

                System.out.println(contractName + args);
            }
            contractChainMappings.put(chainName, contractWrappers);
        }
        return contractChainMappings;
    }


}
