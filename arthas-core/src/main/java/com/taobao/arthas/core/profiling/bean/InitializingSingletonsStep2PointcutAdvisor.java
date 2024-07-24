package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.profiling.AbstractMethodMatchInvokePointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
class InitializingSingletonsStep2PointcutAdvisor extends AbstractMethodMatchInvokePointcutAdvisor implements ClassMethodMatchPointcut, DisposableBean {

    private final ThreadLocal<Stack<String>> INSTANTIATE_SINGLETON_CACHE = ThreadLocal.withInitial(Stack::new);

    @Autowired
    private InitializingSingletonsStep1PointcutAdvisor initializingSingletonsStep1AdviceHandler;

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public InitializingSingletonsStep2PointcutAdvisor() {
        super("org.springframework.beans.factory.support.DefaultSingletonBeanRegistry#getSingleton(java.lang.String)");
    }

    @Override
    public boolean isReady() {
        return initializingSingletonsStep1AdviceHandler.step1Ready();
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
