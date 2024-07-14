package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.stereotype.Component;

@Component
public class InitializingSingletonsStep1AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private final ThreadLocal<Boolean> isReady = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.beans.factory.support.DefaultListableBeanFactory#preInstantiateSingletons()"
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
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        isReady.set(Boolean.TRUE);
    }

    /**
     * 创建Bean成功, 出栈
     *
     * @param invokeVO {@link com.taobao.arthas.spring.listener.BeanCreateReporter}
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        isReady.set(Boolean.FALSE);
    }

}
