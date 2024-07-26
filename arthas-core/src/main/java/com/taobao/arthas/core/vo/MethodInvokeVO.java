package com.taobao.arthas.core.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author linyimin
 **/
@Data
public class MethodInvokeVO {

    private final String methodQualifier;

    private final BigDecimal startMillis;

    private BigDecimal duration;

    private Object[] args;

    public MethodInvokeVO(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;
        startMillis = DurationUtils.nowMillis();

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
