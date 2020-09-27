package org.rahul.dbc.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.rahul.dbc.annotations.Validate;
import org.rahul.dbc.contract.FlatContract;
import org.rahul.dbc.validator.hierarchy.ValidatorFactory;

import javax.inject.Inject;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContractHierarchyInterceptor implements MethodInterceptor {

    private final ValidatorFactory validatorFactory;

    @Inject
    public ContractHierarchyInterceptor(ValidatorFactory validatorFactory) {
        this.validatorFactory = validatorFactory;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Parameter[] parameters = invocation.getMethod().getParameters();
        Map<String, Object> parameterMappings = InterceptorUtils.getParameterMappings(invocation, parameters);

        String[] invariants = invocation.getMethod().getAnnotation(Validate.class).value();
        Map<String, List<FlatContract<?>>> contracts = this.getMethods(invariants);

        for (Map.Entry<String, List<FlatContract<?>>> entry : contracts.entrySet()) {
            System.out.println("Chain argument name -- " + entry.getKey());
            entry.getValue().forEach(invariant -> ((FlatContract<Object>) invariant).validate(parameterMappings.get(entry.getKey())));
        }

        return invocation.proceed();
    }

    private Map<String, List<FlatContract<?>>> getMethods(String[] invariants) {

        Map<String, List<FlatContract<?>>> invariantChainMappings = new HashMap<>();

        for (String s : invariants) {
            String[] chainDefinition = s.split("=");
            String key = chainDefinition[0].strip();

            String[] validatorNames = chainDefinition[1].strip().split(",");

            List<FlatContract<?>> contracts = new ArrayList<>();

            for (String validator : validatorNames) {
                this.validatorFactory.getValidator(validator.strip())
                        .ifPresent(contracts::add);
            }

            invariantChainMappings.put(key, contracts);
        }

        return invariantChainMappings;

    }


}
