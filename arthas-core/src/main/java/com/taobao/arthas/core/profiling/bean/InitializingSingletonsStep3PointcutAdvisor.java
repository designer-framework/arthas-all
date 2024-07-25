package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.events.InstantiateSingletonOverEvent;
import com.taobao.arthas.core.profiling.AbstractMethodMatchInvokePointcutAdvisor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
class InitializingSingletonsStep3PointcutAdvisor extends AbstractMethodMatchInvokePointcutAdvisor implements DisposableBean {

    private final ThreadLocal<Stack<InstantiateSingletonOverEvent>> stackThreadLocal = ThreadLocal.withInitial(Stack::new);

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    private InitializingSingletonsStep2PointcutAdvisor initializingSingletonsStep2AdviceHandler;

    public InitializingSingletonsStep3PointcutAdvisor() {
        super("**#afterSingletonsInstantiated()");
    }

    @Override
    public boolean isReady() {
        return initializingSingletonsStep2AdviceHandler.isReady();
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        if (initializingSingletonsStep2AdviceHandler.hasSmartInitializingSingleton()) {
            stackThreadLocal.get().push(new InstantiateSingletonOverEvent(this, initializingSingletonsStep2AdviceHandler.popBeanName()));
        }
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        //正在加载SmartInitializingSingleton Bean
        if (!stackThreadLocal.get().isEmpty()) {
            eventPublisher.publishEvent(stackThreadLocal.get().pop().instantiated());
        }
    }

    @Override
    public void destroy() {
        stackThreadLocal.remove();
    }

}
