package com.taobao.arthas.spring.vo;

import lombok.Data;

@Data
public class TraceMethodInfo {

    public final String className;
    public final String methodName;
    public final String[] methodArgumentTypes;
    private final String fullyQualifiedMethodName;
    private final int methodArgumentLength;

    public TraceMethodInfo(String fullyQualifiedMethodName, String className, String methodName, String[] methodArgumentTypes) {
        this.fullyQualifiedMethodName = fullyQualifiedMethodName;
        this.className = className;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
        methodArgumentLength = methodArgumentTypes.length;
    }

    public boolean isCandidateClass(String className) {
        return this.className.startsWith(className) || "*".equals(this.className);
    }

    public boolean isCandidateMethod(String methodName, String[] methodArgumentTypes) {
        //
        if (!this.methodName.startsWith(methodName)) {
            return false;
        }

        for (int i = 0; i < methodArgumentLength; i++) {
            if (!this.methodArgumentTypes[i].equals(methodArgumentTypes[i])) {
                return false;
            }
        }

        return true;
    }

}
