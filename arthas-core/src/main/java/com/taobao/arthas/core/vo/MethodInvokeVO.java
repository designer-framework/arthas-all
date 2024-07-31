package com.taobao.arthas.core.vo;

import lombok.Getter;
import lombok.Setter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-01 21:05
 */
@Getter
@Setter
public class MethodInvokeVO extends DurationVO {

    private final String methodQualifier;

    private Object[] args;

    public MethodInvokeVO(String methodQualifier, Object[] args) {
        this.methodQualifier = methodQualifier;

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
