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

    private static final ThreadLocal<Stack<String>> INSTANTIATE_SINGLETON_CACHE = ThreadLocal.withInitial(Stack::new);

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
        return initializingSingletonsStep1AdviceHandler.isReady();
    }

    public String getBeanName() {
        return INSTANTIATE_SINGLETON_CACHE.get().peek();
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        INSTANTIATE_SINGLETON_CACHE.get().push(String.valueOf(invokeVO.getParams()[0]));
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
    }

    @Override
    public void destroy() {
        INSTANTIATE_SINGLETON_CACHE.remove();
    }

}
