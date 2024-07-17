package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.InstantiateSingletonOverEvent;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
class InitializingSingletonsStep3AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private final ThreadLocal<Stack<InstantiateSingletonOverEvent>> stackThreadLocal = ThreadLocal.withInitial(Stack::new);

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    private InitializingSingletonsStep2AdviceHandler initializingSingletonsStep2AdviceHandler;

    public InitializingSingletonsStep3AdviceHandler() {
        super(FullyQualifiedClassUtils.toTraceMethodInfo(
                "*#afterSingletonsInstantiated()"
        ));
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
        stackThreadLocal.get().push(new InstantiateSingletonOverEvent(this, initializingSingletonsStep2AdviceHandler.getBeanName()));
    }

    /**
     * 创建Bean成功, 出栈
     *
     * @param invokeVO {@link com.taobao.arthas.spring.listener.BeanCreateReporter}
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        try {
            Class<?> afterSingletonsInstantiatedClass = Class.forName("org.springframework.beans.factory.SmartInitializingSingleton", true, invokeVO.getLoader());
            boolean isSmartInitializingSingletonBean = afterSingletonsInstantiatedClass.isAssignableFrom(invokeVO.getClazz());
            //是否SmartInitializingSingleton实例
            if (isSmartInitializingSingletonBean) {
                eventPublisher.publishEvent(stackThreadLocal.get().pop().instantiated());
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
