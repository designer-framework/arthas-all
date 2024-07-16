package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
class InitializingSingletonsStep2AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private static final ThreadLocal<Stack<String>> INSTANTIATE_SINGLETON_CACHE = ThreadLocal.withInitial(Stack::new);

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry" +
                    "#getSingleton(java.lang.String)"
    );

    @Autowired
    private InitializingSingletonsStep1AdviceHandler initializingSingletonsStep1AdviceHandler;

    @Override
    public boolean isCandidateClass(String className) {
        return traceMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return traceMethodInfo.isCandidateMethod(methodName, methodArgTypes);
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

}
