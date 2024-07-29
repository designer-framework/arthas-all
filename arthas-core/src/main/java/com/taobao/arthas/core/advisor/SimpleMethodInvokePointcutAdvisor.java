package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import com.taobao.arthas.core.properties.MethodInvokeAdvisor;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 23:00
 * @see com.taobao.arthas.core.configuration.advisor.AgentMethodInvokeRegistryPostProcessor
 */
@Slf4j
public class SimpleMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, Ordered {

    private final ThreadLocal<Map<String, MethodInvokeVO>> methodInvokeMapThreadLocal = ThreadLocal.withInitial(HashMap::new);

    private AgentStatistics agentStatistics;

    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo, Boolean.FALSE, SimpleSpyInterceptorApi.class);
    }

    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(classMethodInfo, Boolean.FALSE, interceptor);
    }

    /**
     * 动这个构造器之前先看注释
     *
     * @param classMethodInfo
     * @param canRetransform
     * @see com.taobao.arthas.core.configuration.advisor.BeanDefinitionRegistryUtils#registry(BeanDefinitionRegistry, MethodInvokeAdvisor)
     */
    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorClass) {
        super(classMethodInfo, canRetransform, spyInterceptorClass);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        //入栈
        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(getClassMethodInfo().getFullyQualifiedMethodName(), getParams(invokeVO));
        methodInvokeMapThreadLocal.get().put(getInvokeKey(invokeVO), methodInvokeVO);

        agentStatistics.addMethodInvoke(methodInvokeVO);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        //出栈
        Map<String, MethodInvokeVO> methodInvokeMap = methodInvokeMapThreadLocal.get();
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            //
            MethodInvokeVO methodInvoke = methodInvokeMap.get(getInvokeKey(invokeVO)).initialized();
            //
            atExitAfter(invokeVO, methodInvoke);
        }

    }

    protected void atExitAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
    }

    protected Object[] getParams(InvokeVO invokeVO) {
        return invokeVO.getParams();
    }

    protected String getInvokeKey(InvokeVO invokeVO) {
        return invokeVO.getHeadInvokeId() + ":" + invokeVO.getInvokeId();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.agentStatistics = applicationContext.getBean(AgentStatistics.class);
        Assert.notNull(agentStatistics, "AgentStatistics");
    }

    @Override
    public void destroy() {
        methodInvokeMapThreadLocal.remove();
    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.RELEASE_METHOD_INVOKE;
    }

}
