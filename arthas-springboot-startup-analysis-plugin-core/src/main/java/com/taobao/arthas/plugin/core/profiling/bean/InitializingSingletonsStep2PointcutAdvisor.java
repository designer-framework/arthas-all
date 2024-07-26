package com.taobao.arthas.plugin.core.profiling.bean;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Stack;

public class InitializingSingletonsStep2PointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    private final ThreadLocal<Stack<String>> INSTANTIATE_SINGLETON_CACHE = ThreadLocal.withInitial(Stack::new);

    private final InitializingSingletonsStep1PointcutAdvisor initializingSingletonsStep1AdviceHandler;

    public InitializingSingletonsStep2PointcutAdvisor(InitializingSingletonsStep1PointcutAdvisor initializingSingletonsStep1AdviceHandler) {
        this.initializingSingletonsStep1AdviceHandler = initializingSingletonsStep1AdviceHandler;
    }

    @Override
    public boolean isReady() {
        return super.isReady() && initializingSingletonsStep1AdviceHandler.step1Ready();
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        try {
            Class<?> afterSingletonsInstantiatedClass = Class.forName("org.springframework.beans.factory.SmartInitializingSingleton", true, invokeVO.getLoader());
            //是否SmartInitializingSingleton实例
            if (invokeVO.getReturnObj() != null && afterSingletonsInstantiatedClass.isAssignableFrom(invokeVO.getReturnObj().getClass())) {
                INSTANTIATE_SINGLETON_CACHE.get().push(String.valueOf(invokeVO.getParams()[0]));
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String popBeanName() {
        return INSTANTIATE_SINGLETON_CACHE.get().pop();
    }

    public boolean hasSmartInitializingSingleton() {
        return !INSTANTIATE_SINGLETON_CACHE.get().isEmpty();
    }

    @Override
    public void destroy() {
        INSTANTIATE_SINGLETON_CACHE.remove();
    }

}
