package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.profiling.AbstractInvokeAdviceHandler;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
class InitializingSingletonsStep2AdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, DisposableBean {

    private static final ThreadLocal<Stack<String>> INSTANTIATE_SINGLETON_CACHE = ThreadLocal.withInitial(Stack::new);

    @Autowired
    private InitializingSingletonsStep1AdviceHandler initializingSingletonsStep1AdviceHandler;

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public InitializingSingletonsStep2AdviceHandler() {
        super(FullyQualifiedClassUtils.parserClassMethodInfo(
                "org.springframework.beans.factory.support.DefaultSingletonBeanRegistry" +
                        "#getSingleton(java.lang.String)"
        ));
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
    public void destroy() throws Exception {
        INSTANTIATE_SINGLETON_CACHE.remove();
    }
    
}