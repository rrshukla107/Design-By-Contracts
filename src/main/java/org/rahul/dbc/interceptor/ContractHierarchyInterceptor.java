package org.rahul.dbc.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.rahul.dbc.annotations.BiValidate;
import org.rahul.dbc.annotations.PostValidate;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.engine.BiContractWrapper;
import org.rahul.dbc.engine.ChainResult;
import org.rahul.dbc.engine.ContractChainExecutor;
import org.rahul.dbc.engine.SingleArgContractWrapper;
import org.rahul.dbc.report.TextReportGenerator;
import org.rahul.dbc.validator.ValidatorFactory;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.*;
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

        long startTime = System.nanoTime();
        Map<String, CompletableFuture<ChainResult>> resultMappings = executePreconditions(invocation, parameterMappings);
        long endTime = System.nanoTime();

        boolean hasFailed = hasContractFailed(resultMappings);
        //Mock code for reporting -- think about this
        String preConditionsExecutionResult = getContractChainDetails(resultMappings);


        System.out.println("-------");
        System.out.println(new TextReportGenerator().generatePreConditionReport(resultMappings, 10d));
        System.out.println("-------");


        System.out.println("\n##############PRE-CONDITIONS####################\n");
        if (hasFailed) {
            throw new RuntimeException(preConditionsExecutionResult);
        }
        System.out.println(preConditionsExecutionResult);
        Object result = invocation.proceed();

        parameterMappings.put("*", result);

        if (!"void".equals(invocation.getMethod().getReturnType().getName())) {
            this.evaluatePostCondition(result, invocation, parameterMappings);
        }

        return result;
    }

    private Map<String, CompletableFuture<ChainResult>> executePreconditions(MethodInvocation invocation, Map<String, Object> parameterMappings) throws InterruptedException, ExecutionException {
        // check whether annotations exist or not??
        Map<String, CompletableFuture<ChainResult>> resultMappings = this.executeSingleArgContracts(invocation, parameterMappings);

        resultMappings.putAll(this.executeBiContracts(invocation, parameterMappings));

        //Join
        CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();
        return resultMappings;
    }

    private Map<String, CompletableFuture<ChainResult>> executeBiContracts(MethodInvocation invocation, Map<String, Object> parameterMappings) {

        return Optional.ofNullable(invocation.getMethod().getAnnotation(BiValidate.class)).map(annotation -> annotation.value()).map(biInvariants -> {
            Map<String, List<BiContractWrapper<?, ?>>> biContracts = ParserUtil.getBiArgContractWrapper(biInvariants, parameterMappings, this.validatorFactory);
            return this.executeBiContracts(biContracts);
        }).orElse(Collections.emptyMap());

    }

    private Map<String, CompletableFuture<ChainResult>> executeSingleArgContracts(MethodInvocation invocation, Map<String, Object> parameterMappings) throws InterruptedException, ExecutionException {

        return Optional.ofNullable(invocation.getMethod().getAnnotation(Validate.class)).map(annotation -> annotation.value()).map(invariants -> {
            Map<String, List<SingleArgContractWrapper<?>>> singleArgContracts = ParserUtil.getContractWrapper(invariants, parameterMappings, this.validatorFactory);
            return this.executeSingleArgContracts(singleArgContracts);
        }).orElse(Collections.emptyMap());


    }

    private void evaluatePostCondition(Object result, MethodInvocation invocation, Map<String, Object> parameterMappings) throws ExecutionException, InterruptedException {
        String[] invariants = invocation.getMethod().getAnnotation(PostValidate.class).value();

//        invocation.getMethod().getAnnotations()

        Map<String, List<SingleArgContractWrapper<?>>> contractWrappers = ParserUtil.getContractWrapper(invariants, parameterMappings, this.validatorFactory);
        Map<String, CompletableFuture<ChainResult>> resultMappings = executeSingleArgContracts(contractWrappers);

        CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();

        boolean hasFailed = hasContractFailed(resultMappings);
        //Mock code for reporting -- think about this
        String postConditionsExecutionResult = getContractChainDetails(resultMappings);

        if (hasFailed) {
            throw new RuntimeException(postConditionsExecutionResult);
        }
        System.out.println("\n##############POST-CONDITIONS####################\n");

        System.out.println(postConditionsExecutionResult);
    }

    private Map<String, CompletableFuture<ChainResult>> executeSingleArgContracts(Map<String, List<SingleArgContractWrapper<?>>> contracts) {
        Map<String, CompletableFuture<ChainResult>> resultMappings = new HashMap<>();

        //Fork
        for (Map.Entry<String, List<SingleArgContractWrapper<?>>> entry : contracts.entrySet()) {
            List<SingleArgContractWrapper<Object>> typeCastedContracts = entry.getValue().stream()
                    .map(contract -> (SingleArgContractWrapper<Object>) contract)
                    .collect(Collectors.toList());
            resultMappings.put(entry.getKey(), this.contractChainExecutor.executeContractChain(typeCastedContracts));
        }

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
            result.append("**************CHAIN - " + entry.getKey() + "**************\n");
            if (chainResult.isSuccessful()) {
                result.append("PASSED SUCCESSFULLY\n");
            } else {
                result.append("FAILED\n");
                result.append("FAILED CONTRACT NAME - " + chainResult.getFailedContractName().get() + "\n");

                if (!chainResult.getUnderlyingException().isPresent()) {
                    result.append("Contract Failed due to Validation Failure \n");
                } else {
                    result.append("Contract Failed due to underlying exception - \n");
                    chainResult.getUnderlyingException()
                            .map(ExceptionUtils::getStackTrace)
                            .ifPresent(result::append);
                    result.append("\n");
                }
            }
            entry.getValue().get().getExecutionTimes().entrySet().stream().forEach(e -> result.append(e.getKey() + " " + e.getValue() + "ms \n"));
            System.out.println("******************************************\n");

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


}
