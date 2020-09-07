package org.example;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import javax.inject.Inject;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class Interceptor implements MethodInterceptor {

    private Validators validators;

    @Inject
    Interceptor(Validators validators) {
        this.validators = validators;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        Method[] declaredMethods = validators.getClass().getDeclaredMethods();

        Map<String, Method> methods = new HashMap<>();

        for (Method method : declaredMethods) {
            methods.put(method.getName(), method);
        }

        String multiParamValidators = invocation.getMethod().getAnnotation(ValidateArg.class).value();

        System.out.println("found - " + multiParamValidators);
        for (Parameter parameter : invocation.getMethod().getParameters()) {
            String singleArgValidator = parameter.getAnnotation(ValidateArg.class).value();
            System.out.println(singleArgValidator);

            methods.get(singleArgValidator).invoke(this.validators, invocation.getArguments()[0]);
        }


        return invocation.proceed();

    }
}
