package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.interceptor.InvokeInterceptorAdapter;
import com.taobao.arthas.api.pointcut.CachingPointcut;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.source.AgentSourceAttribute;
import com.taobao.arthas.api.state.AgentState;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.util.Assert;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
@Slf4j
public abstract class AbstractMethodInvokePointcutAdvisor extends InvokeInterceptorAdapter implements ApplicationEventPublisherAware, PointcutAdvisor, InitializingBean {

    @Setter
    protected ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    protected AgentState agentState;

    @Setter
    private Pointcut pointcut = Pointcut.FALSE;

    @Getter
    @Setter
    private AgentSourceAttribute agentSourceAttribute;

    public AbstractMethodInvokePointcutAdvisor() {
    }

    public AbstractMethodInvokePointcutAdvisor(String fullyQualifiedMethodName) {
        this(ClassMethodInfo.create(fullyQualifiedMethodName));
    }

    public AbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        this(classMethodInfo, Boolean.FALSE);
    }

    public AbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform) {
        this.agentSourceAttribute = new AgentSourceAttribute(classMethodInfo);
        pointcut = new CachingPointcut(agentSourceAttribute, canRetransform);
    }

    public boolean isReady() {
        return agentState.isStarted();
    }

    protected abstract void atBefore(InvokeVO invokeVO);

    @Override
    public void before(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atBefore(invokeVO);
        }
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atAfterReturning(invokeVO);
        }
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atAfterThrowing(invokeVO);
        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected void atAfterThrowing(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected abstract void atExit(InvokeVO invokeVO);

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

    protected ClassMethodInfo getClassMethodInfo() {
        return agentSourceAttribute.getSourceAttribute();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(agentState, "AgentState");
        if (pointcut == Pointcut.FALSE) {
            log.error("默认的Pointcut,  请检查配置是否正确: {}", getClass());
        }
    }

}
