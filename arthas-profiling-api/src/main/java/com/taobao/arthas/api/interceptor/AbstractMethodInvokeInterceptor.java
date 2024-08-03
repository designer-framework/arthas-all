package com.taobao.arthas.api.interceptor;

import com.taobao.arthas.api.vo.InvokeVO;

/**
 * 参见
 * {@link com.taobao.arthas.core.AdviceListenerAdapter}
 */
public abstract class AbstractMethodInvokeInterceptor implements InvokeInterceptor {

    public abstract boolean isReady(InvokeVO invokeVO);

    @Override
    public void before(InvokeVO invokeVO) throws Throwable {
        if (isReady(invokeVO)) {
            atBefore(invokeVO);
        }
    }

    protected abstract void atBefore(InvokeVO invokeVO) throws Throwable;

    @Override
    public void afterReturning(InvokeVO invokeVO) throws Throwable {
        if (isReady(invokeVO)) {
            atAfterReturning(invokeVO);
        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) throws Throwable {
        atExit(invokeVO);
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) throws Throwable {
        if (isReady(invokeVO)) {
            atAfterThrowing(invokeVO);
        }
    }

    protected void atAfterThrowing(InvokeVO invokeVO) throws Throwable {
        atExit(invokeVO);
    }

    protected abstract void atExit(InvokeVO invokeVO) throws Throwable;

}
