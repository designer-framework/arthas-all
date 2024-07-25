package com.taobao.arthas.core.profiling.bean;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.events.BeanAopProxyCreatedEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class SpringBeanAopProxyPointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    private static final ThreadLocal<Stack<AopInfo>> STACK_THREAD_LOCAL = ThreadLocal.withInitial(Stack::new);

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    /**
     * org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
     */
    public SpringBeanAopProxyPointcutAdvisor() {
        super("org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)");
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

    @Override
    protected void atExit(InvokeVO invokeVO) {
        //发布Bean创建成功事件
        AopInfo aopInfo = STACK_THREAD_LOCAL.get().pop();
        if (!(invokeVO.getReturnObj() == invokeVO.getParams()[0])) {
            eventPublisher.publishEvent(new BeanAopProxyCreatedEvent(this, aopInfo.beanName, aopInfo.startTime));
        }
    }

    @Override
    public void destroy() {
        STACK_THREAD_LOCAL.remove();
    }

    static class AopInfo {

        private final long startTime;

        private final String beanName;

        public AopInfo(String beanName) {
            this.beanName = beanName;
            startTime = System.currentTimeMillis();
        }

    }

}
