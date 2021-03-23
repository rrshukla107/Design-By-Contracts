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
import org.rahul.dbc.report.ReportGenerator;
import org.rahul.dbc.validator.ValidatorFactory;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class ContractHierarchyInterceptor implements MethodInterceptor {

    public static final String INVOCATION_RESULT = "*";
    public static final String VOID = "void";
    public static final double MILLIS = 1000000d;

    private final ValidatorFactory validatorFactory;
    private final ContractChainExecutor contractChainExecutor;
    private final ReportGenerator reportGenerator;

    @Inject
    public ContractHierarchyInterceptor(final ValidatorFactory validatorFactory, final ContractChainExecutor contractChainExecutor, final ReportGenerator reportGenerator) {
        this.validatorFactory = validatorFactory;
        this.contractChainExecutor = contractChainExecutor;
        this.reportGenerator = reportGenerator;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> parameterMappings = InterceptorUtils.getParameterMappings(invocation, parameters);

        executePreConditions(invocation, parameterMappings);
        Object result = invocation.proceed();

        parameterMappings.put(INVOCATION_RESULT, result);
        executePostConditions(invocation, parameterMappings, result);

        return result;
    }

    private void executePreConditions(MethodInvocation invocation, Map<String, Object> parameterMappings) throws InterruptedException, ExecutionException {
        long preConditionsStartTime = System.nanoTime();
        Map<String, CompletableFuture<ChainResult>> resultMappings = executePreconditions(invocation, parameterMappings);
        long preConditionsEndTime = System.nanoTime();

        String preConditionsResult = this.reportGenerator.generatePreConditionReport(resultMappings, (preConditionsEndTime - preConditionsStartTime) / MILLIS);

        if (this.hasContractFailed(resultMappings)) {
            throw new RuntimeException(preConditionsResult);
        } else {
            System.out.println(preConditionsResult);
        }
    }

    private void executePostConditions(MethodInvocation invocation, Map<String, Object> parameterMappings, Object result) throws ExecutionException, InterruptedException {
        if (!VOID.equals(invocation.getMethod().getReturnType().getName())) {
            long postConditionsStartTime = System.nanoTime();
            Map<String, CompletableFuture<ChainResult>> postConditionResultMappings = this.evaluatePostCondition(result, invocation, parameterMappings);
            long postConditionsEndTime = System.nanoTime();

            boolean postContractFailed = hasContractFailed(postConditionResultMappings);
            String postContractResult = this.reportGenerator.generatePostConditionReport(postConditionResultMappings, (postConditionsEndTime - postConditionsStartTime) / MILLIS);

            if (this.hasContractFailed(postConditionResultMappings)) {
                throw new RuntimeException(postContractResult);
            } else {
                System.out.println(postContractResult);
            }

        }
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

    private Map<String, CompletableFuture<ChainResult>> evaluatePostCondition(Object result, MethodInvocation invocation, Map<String, Object> parameterMappings) throws ExecutionException, InterruptedException {

        return Optional.ofNullable(invocation.getMethod().getAnnotation(PostValidate.class)).map(i -> {

            String[] invariants = i.value();
            Map<String, List<SingleArgContractWrapper<?>>> contractWrappers = ParserUtil.getContractWrapper(invariants, parameterMappings, this.validatorFactory);
            Map<String, CompletableFuture<ChainResult>> resultMappings = executeSingleArgContracts(contractWrappers);

            try {
                CompletableFuture.allOf(resultMappings.values().stream().toArray(CompletableFuture[]::new)).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return resultMappings;

        }).orElse(Collections.emptyMap());

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
