package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import org.springframework.stereotype.Component;

@Component
public class InitializingSingletonsStep1AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private final ThreadLocal<Boolean> isReady = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public InitializingSingletonsStep1AdviceHandler() {
        super(
                FullyQualifiedClassUtils.toTraceMethodInfo(
                        "org.springframework.beans.factory.support.DefaultListableBeanFactory" +
                                "#preInstantiateSingletons()"
                )
        );
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
