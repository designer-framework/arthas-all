package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.events.InstantiateSingletonOverEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.util.Stack;

public class InitializingSingletonsStep3PointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    private final ThreadLocal<Stack<InstantiateSingletonOverEvent>> eventThreadLocal = ThreadLocal.withInitial(Stack::new);

    private final InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2AdviceHandler;

    public InitializingSingletonsStep3PointcutAdvisor(InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2AdviceHandler) {
        this.initializingSingletonsStep2AdviceHandler = initializingSingletonsStep2AdviceHandler;
    }

    @Override
    public boolean isReady() {
        return super.isReady() && initializingSingletonsStep2AdviceHandler.isReady();
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        if (initializingSingletonsStep2AdviceHandler.hasSmartInitializingSingleton()) {
            eventThreadLocal.get().push(new InstantiateSingletonOverEvent(this, initializingSingletonsStep2AdviceHandler.popBeanName()));
        }
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        //正在加载SmartInitializingSingleton Bean
        if (!eventThreadLocal.get().isEmpty()) {
            applicationEventPublisher.publishEvent(eventThreadLocal.get().pop().instantiated());
        }
    }

    @Override
    public void destroy() {
        eventThreadLocal.remove();
    }

}
