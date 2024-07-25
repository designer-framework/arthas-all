package com.taobao.arthas.core.vo;

import lombok.Data;
import org.springframework.util.AntPathMatcher;

import java.util.Objects;

@Data
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

}
