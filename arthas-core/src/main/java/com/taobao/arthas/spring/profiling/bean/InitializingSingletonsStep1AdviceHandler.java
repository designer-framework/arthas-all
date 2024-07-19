package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.profiling.AbstractInvokeAdviceHandler;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class InitializingSingletonsStep1AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, DisposableBean {

    private final ThreadLocal<Boolean> isReady = ThreadLocal.withInitial(() -> Boolean.FALSE);

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public InitializingSingletonsStep1AdviceHandler() {
        super(
                FullyQualifiedClassUtils.parserClassMethodInfo(
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

    @Override
    protected void atExit(InvokeVO invokeVO) {
        isReady.set(Boolean.FALSE);
    }

    @Override
    public void destroy() throws Exception {
        isReady.remove();
    }
    
}
