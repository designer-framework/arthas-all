package com.taobao.arthas.api.vo;

import org.springframework.util.AntPathMatcher;

import java.util.Arrays;
import java.util.Objects;

public class ClassMethodInfo {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher(".");

    public final String className;

    public final String methodName;

    public final String[] methodArgumentTypes;

    private final String fullyQualifiedMethodName;

    private final int methodArgumentLength;

    public ClassMethodInfo(String fullyQualifiedMethodName, String className, String methodName, String[] methodArgumentTypes) {
        this.fullyQualifiedMethodName = fullyQualifiedMethodName;
        this.className = className;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
        methodArgumentLength = methodArgumentTypes.length;
    }

    public static ClassMethodInfo create(String fullyQualifiedMethodName) {
        return getClassMethodInfo(fullyQualifiedMethodName);
    }

    /**
     * @param fullyQualifiedMethodName
     * @return
     * @see ClassMethodInfo#getClassMethodInfo(String)
     */
    private static ClassMethodInfo getClassMethodInfo(String fullyQualifiedMethodName) {
        //类命
        String className = fullyQualifiedMethodName.split("#")[0].trim();
        //方法名
        String[] method_arguments = fullyQualifiedMethodName.split("#")[1].split("\\(");
        String methodName = method_arguments[0].trim();
        //入参类型
        String methodArgumentsStr = method_arguments[1].replace(")", "");
        String[] methodArguments = methodArgumentsStr.split(",");
        if (methodArgumentsStr.isEmpty()) {
            methodArguments = new String[0];
        }
        for (int i = 0; i < methodArguments.length; i++) {
            methodArguments[i] = methodArguments[i].trim();
        }

        return new ClassMethodInfo(
                fullyQualifiedMethodName
                , className, methodName, Arrays.stream(methodArguments).toArray(String[]::new)
        );

    }

    public boolean isCandidateClass(String className) {
        return antPathMatcher.match(this.className, className);
    }

    public boolean isCandidateMethod(String methodName, String[] methodArgumentTypes) {
        //
        if (!antPathMatcher.match(this.methodName, methodName)) {
            return false;
        }

        if (methodArgumentLength != methodArgumentTypes.length) {
            return false;
        }

        for (int i = 0; i < methodArgumentLength; i++) {
            if (!this.methodArgumentTypes[i].equals(methodArgumentTypes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean equals(Object that) {
        return Objects.equals(fullyQualifiedMethodName, that);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullyQualifiedMethodName);
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getMethodArgumentTypes() {
        return methodArgumentTypes;
    }

    public String getFullyQualifiedMethodName() {
        return fullyQualifiedMethodName;
    }

    public int getMethodArgumentLength() {
        return methodArgumentLength;
    }

}