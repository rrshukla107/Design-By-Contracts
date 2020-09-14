package org.example;

import java.lang.reflect.Method;
import java.util.List;

public class Contract {

    private String methodName;
    private Method method;
    private List<String> arguments;

    public Contract(String methodName, Method method, List<String> arguments) {
        this.methodName = methodName;
        this.method = method;
        this.arguments = arguments;
    }


    public String getMethodName() {
        return methodName;
    }

    public Method getMethod() {
        return method;
    }

    public List<String> getArguments() {
        return arguments;
    }
}
