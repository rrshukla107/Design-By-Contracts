package org.example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Interceptor implements MethodInterceptor {

    private Validators validators;

    @Inject
    Interceptor(Validators validators) {
        this.validators = validators;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {


        // STEP 1 - Get all the validators method invocations in a map
        Map<String, Method> methods = getValidators(this.validators);

        // STEP 2 - Go over all the arguments to find the single field validators
        Parameter[] parameters = invocation.getMethod().getParameters();

        // STEP 3 - For each of the single field validators, create the contract
        List<Contract> contracts = getSingleArgumentContracts(methods, parameters);

        // STEP 4 - Go over the method to find all the multiple field validators
        String[] multipleFieldValidators = invocation.getMethod().getAnnotation(ValidateMultipleArgs.class).value();

        // STEP 5 - Parse the method to find all the arguments
        // STEP 6 - For each multiple field validators, create the contract
        contracts.addAll(getMultipleFieldContracts(methods, multipleFieldValidators));

        // STEP 7 - Execute the contracts
        Map<String, Object> argumentMappings = getParameterMappings(invocation, parameters);
        executeContracts(contracts, argumentMappings);

        // STEP 8 - Continue with the invocation
        return invocation.proceed();

    }

    private List<Contract> getMultipleFieldContracts(Map<String, Method> methods, String[] multipleFieldValidators) {
        return Stream.of(multipleFieldValidators).map(validator -> {
            String validatorName = getMultipleArgValidatorName(validator);
            return new Contract(validatorName, methods.get(validatorName), this.getMultipleArgValidatorParamNames(validator));
        }).collect(Collectors.toList());
    }

    private List<String> getMultipleArgValidatorParamNames(String validator) {
        return Stream.of(validator.substring(validator.indexOf('(') + 1, validator.indexOf(')')).split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private String getMultipleArgValidatorName(String validator) {
        return validator.substring(0, validator.indexOf('(')).trim();
    }

    private List<Contract> getSingleArgumentContracts(Map<String, Method> methods, Parameter[] parameters) {
        return Stream.of(parameters)
                .map(parameter -> this.getSingleFieldContracts(methods, parameter.getName(),
                        List.of(parameter.getAnnotation(ValidateArg.class).value())))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    private void executeContracts(List<Contract> contracts, Map<String, Object> argumentMappings) {
        contracts.forEach(contract -> {
            try {
                contract.getMethod().
                        invoke(validators, this.getArgs(contract.getArguments(), argumentMappings));
                System.out.println("________________");
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }

    private Map<String, Object> getParameterMappings(MethodInvocation invocation, Parameter[] parameters) {
        List<String> parameterNames = Stream.of(parameters).map(Parameter::getName).collect(Collectors.toList());
        Object[] arguments = invocation.getArguments();

        Map<String, Object> argumentMappings = new HashMap<>();

        for (int i = 0; i < parameterNames.size(); i++) {
            argumentMappings.put(parameterNames.get(i), arguments[i]);
        }
        return argumentMappings;
    }

    Object[] getArgs(List<String> argumentNames, Map<String, Object> mappings) {

        Object[] arguments = new Object[argumentNames.size()];

        for (int i = 0; i < argumentNames.size(); i++) {
            arguments[i] = mappings.get(argumentNames.get(i));
        }

        return arguments;
    }


    private Map<String, Method> getValidators(Validators validators) {
        Method[] declaredMethods = validators.getClass().getDeclaredMethods();

        Map<String, Method> methods = new HashMap<>();

        for (Method method : declaredMethods) {
            methods.put(method.getName(), method);
        }
        return methods;
    }

    private List<Contract> getSingleFieldContracts(Map<String, Method> validators, String argumentName, List<String> validatorNames) {

        return validatorNames.stream()
                .map(validators::get)
                .map(method -> new Contract(method.getName(), method, List.of(argumentName)))
                .collect(Collectors.toList());

    }

}
