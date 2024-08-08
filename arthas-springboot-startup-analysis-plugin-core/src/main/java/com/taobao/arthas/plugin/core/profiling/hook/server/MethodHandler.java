package com.taobao.arthas.plugin.core.profiling.hook.server;

import lombok.Data;

import java.lang.reflect.Method;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
@Data
public class MethodHandler implements Handler {

    private Object bean;

    private Method method;

    public MethodHandler(Object bean, Method method) {
        this.bean = bean;
        this.method = method;
    }

    @Override
    public Object handler(Object param) throws Exception {
        return method.invoke(bean, param);
    }

}
