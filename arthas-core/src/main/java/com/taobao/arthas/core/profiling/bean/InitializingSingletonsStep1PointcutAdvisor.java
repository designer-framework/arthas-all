package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.profiling.AbstractMethodMatchInvokePointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class InitializingSingletonsStep1PointcutAdvisor extends AbstractMethodMatchInvokePointcutAdvisor implements DisposableBean {

    private final ThreadLocal<Boolean> isReady = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public InitializingSingletonsStep1PointcutAdvisor() {
        super("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()");
    }

    /**
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        isReady.set(Boolean.TRUE);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        isReady.set(Boolean.FALSE);
    }

    @Override
    public void destroy() {
        isReady.remove();
    }

}
