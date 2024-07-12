package com.taobao.arthas.spring.vo;

public class TraceMethodInfo {

    private final int methodArgumentLength;

    public String className;

    public String methodName;

    public String[] methodArgumentTypes;

    public TraceMethodInfo(String className, String methodName, String[] methodArgumentTypes) {
        this.className = className;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
        this.methodArgumentLength = methodArgumentTypes.length;
    }

    public boolean isCandidateClass(String className) {
        return this.className.startsWith(className);
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
