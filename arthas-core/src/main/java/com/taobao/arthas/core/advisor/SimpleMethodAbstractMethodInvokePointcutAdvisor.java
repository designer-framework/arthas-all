package com.taobao.arthas.core.advisor;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import com.taobao.arthas.core.properties.MethodInvokeWatchProperties;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 23:00
 * @see com.taobao.arthas.core.configuration.advisor.AgentMethodInvokeRegistryPostProcessor
 */
@Slf4j
public class SimpleMethodAbstractMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, Ordered {

    private final Map<String, MethodInvokeVO> methodInvokeMap = new ConcurrentHashMap<>();

    private AgentStatistics agentStatistics;

    public SimpleMethodAbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo, Boolean.FALSE, SimpleSpyInterceptorApi.class);
    }

    public SimpleMethodAbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(classMethodInfo, Boolean.FALSE, interceptor);
    }

    /**
     * 动这个构造器之前先看注释
     *
     * @param classMethodInfo
     * @param canRetransform
     * @see com.taobao.arthas.core.configuration.advisor.BeanDefinitionRegistryUtils#registry(BeanDefinitionRegistry, MethodInvokeWatchProperties)
     */
    public SimpleMethodAbstractMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorClass) {
        super(classMethodInfo, canRetransform, spyInterceptorClass);
    }

    @Override
    protected final void atBefore(InvokeVO invokeVO) {

        //入栈
        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(getClassMethodInfo().getFullyQualifiedMethodName(), getParams(invokeVO));
        methodInvokeMap.put(getInvokeKey(invokeVO), methodInvokeVO);

        atMethodInvokeBefore(invokeVO, methodInvokeVO);

        agentStatistics.addMethodInvoke(methodInvokeVO);

    }

    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
    }

    @Override
    protected final void atExit(InvokeVO invokeVO) {

        //出栈
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            //
            MethodInvokeVO methodInvoke = methodInvokeMap.get(getInvokeKey(invokeVO));
            methodInvoke.initialized();

            atMethodInvokeAfter(invokeVO, methodInvoke);

        } else {

            log.error("Predecessor node not found: {}", JSON.toJSONString(invokeVO));

        }

    }

    /**
     * 组件加载完毕
     *
     * @param invokeVO
     * @param methodInvokeVO
     */
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
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
        methodInvokeMap.clear();
    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.RELEASE_METHOD_INVOKE;
    }

}
