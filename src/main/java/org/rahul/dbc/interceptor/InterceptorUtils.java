package org.rahul.dbc.interceptor;

import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InterceptorUtils {

    public static Map<String, Object> getParameterMappings(MethodInvocation invocation, Parameter[] parameters) {
        List<String> parameterNames = Stream.of(parameters).map(Parameter::getName).collect(Collectors.toList());
        Object[] arguments = invocation.getArguments();

        Map<String, Object> argumentMappings = new HashMap<>();

        for (int i = 0; i < parameterNames.size(); i++) {
            argumentMappings.put(parameterNames.get(i), arguments[i]);
        }
        return argumentMappings;
    }
}
