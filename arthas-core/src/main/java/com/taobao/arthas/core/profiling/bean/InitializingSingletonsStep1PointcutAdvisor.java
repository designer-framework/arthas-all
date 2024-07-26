package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class InitializingSingletonsStep1PointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    private final ThreadLocal<Boolean> isReady = ThreadLocal.withInitial(() -> Boolean.FALSE);

    public InitializingSingletonsStep1PointcutAdvisor() {
        super("org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()");
    }

    boolean step1Ready() {
        return isReady.get();
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
