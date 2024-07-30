package com.taobao.arthas.api.interceptor;

import com.taobao.arthas.api.vo.InvokeVO;

/**
 * 参见
 * {@link com.taobao.arthas.core.AdviceListenerAdapter}
 */
public abstract class InvokeInterceptorAdapter implements InvokeInterceptor {

    @Override
    public void create() {
        //ignore
    }

    @Override
    public abstract void before(InvokeVO invokeVO) throws Throwable;

    @Override
    public abstract void afterReturning(InvokeVO invokeVO) throws Throwable;

    @Override
    public abstract void afterThrowing(InvokeVO invokeVO) throws Throwable;

    @Override
    public void destroy() {
        //ignore
    }

}
