package com.taobao.arthas.spring.vo;

public class TraceMethodProperty {
    public String className;
    public String methodName;
    public String[] methodArgumentTypes;

    public TraceMethodProperty(String className, String methodName, String[] methodArgumentTypes) {
        this.className = className;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
    }
    
}
