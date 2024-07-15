package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.BeanAopProxyCreatedEvent;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class BeanAopProxyAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private static final ThreadLocal<Stack<AopInfo>> STACK_THREAD_LOCAL = ThreadLocal.withInitial(Stack::new);

    /**
     * org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     */
    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator" +
                    "#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)"
    );

    @Override
    public boolean isCandidateClass(String className) {
        return traceMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return traceMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        //发布Bean创建事件
        STACK_THREAD_LOCAL.get().push(new AopInfo(String.valueOf(invokeVO.getParams()[1])));
    }

    /**
     * 创建Bean成功, 出栈
     *
     * @param invokeVO {@link com.taobao.arthas.spring.listener.BeanCreateReporter}
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        //发布Bean创建成功事件
        AopInfo aopInfo = STACK_THREAD_LOCAL.get().pop();
        if (!(invokeVO.getReturnObj() == invokeVO.getParams()[0])) {
            eventPublisher.publishEvent(new BeanAopProxyCreatedEvent(this, aopInfo.beanName, aopInfo.startTime));
        }
    }

    static class AopInfo {

        private final long startTime;

        private final String beanName;

        public AopInfo(String beanName) {
            this.beanName = beanName;
            this.startTime = System.currentTimeMillis();
        }

    }

}
