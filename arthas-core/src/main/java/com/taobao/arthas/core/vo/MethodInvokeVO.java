package com.taobao.arthas.core.vo;

import lombok.Data;

/**
 * @author linyimin
 **/
@Data
public class MethodInvokeVO {

    private final String methodQualifier;

    private final long startMillis;

    private long duration;

    private Object[] args;

    public MethodInvokeVO(String methodQualifier, long startMillis, long duration) {
        this.methodQualifier = methodQualifier;
        this.startMillis = startMillis;
        this.duration = duration;
    }

    public MethodInvokeVO(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;
        startMillis = System.currentTimeMillis();

        if (args == null) {
            return;
        }


        Object[] argStrList = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg != null) {
                argStrList[i] = arg.toString();
            }
        }
        this.args = argStrList;
    }

}
