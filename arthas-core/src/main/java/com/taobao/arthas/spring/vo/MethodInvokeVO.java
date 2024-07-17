package com.taobao.arthas.spring.vo;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author linyimin
 **/
@Data
public class MethodInvokeVO {

    private final String fullyQualifiedMethodName;

    private final long startMillis;

    private long duration;

    private Object[] args;

    public MethodInvokeVO(String fullyQualifiedMethodName, long startMillis, long duration) {
        this.fullyQualifiedMethodName = fullyQualifiedMethodName;
        this.startMillis = startMillis;
        this.duration = duration;
    }

    public MethodInvokeVO(String fullyQualifiedMethodName, Object[] args) {
        this.fullyQualifiedMethodName = fullyQualifiedMethodName;
        startMillis = System.currentTimeMillis();

        if (args == null) {
            return;
        }


        Object[] argStrList = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            try {
                argStrList[i] = JSON.toJSONString(args[i]);
            } catch (Throwable ignored) {
                argStrList[i] = args[i].toString();
            }
        }
        this.args = argStrList;
    }

}
