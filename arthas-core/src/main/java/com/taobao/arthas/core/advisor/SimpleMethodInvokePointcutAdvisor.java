package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.core.vo.DurationUtils;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 23:00
 * @see com.taobao.arthas.core.configuration.advisor.AgentMethodInvokeRegistryPostProcessor
 */
@Slf4j
public class SimpleMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, Ordered, InitializingBean {

    private final ThreadLocal<Map<String, MethodInvokeVO>> methodInvokeMapThreadLocal = ThreadLocal.withInitial(HashMap::new);

    @Autowired
    private AgentStatistics agentStatistics;

    /**
     * @param classMethodInfo
     * @param canRetransform
     * @see com.taobao.arthas.core.configuration.advisor.AgentMethodInvokeRegistryPostProcessor
     */
    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo, Boolean canRetransform) {
        super(classMethodInfo, canRetransform);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {

        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(getClassMethodInfo().getFullyQualifiedMethodName(), invokeVO.getParams());
        methodInvokeMapThreadLocal.get().put(getInvokeKey(invokeVO), methodInvokeVO);

        agentStatistics.addMethodInvoke(methodInvokeVO);

    }

    @Override
    protected void atExit(InvokeVO invokeVO) {

        Map<String, MethodInvokeVO> methodInvokeMap = methodInvokeMapThreadLocal.get();
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            MethodInvokeVO invokeDetail = methodInvokeMap.get(getInvokeKey(invokeVO));
            invokeDetail.setDuration(DurationUtils.nowMillis().subtract(invokeDetail.getStartMillis()));
        }

    }

    protected String getInvokeKey(InvokeVO invokeVO) {
        return invokeVO.getHeadInvokeId() + ":" + invokeVO.getInvokeId();
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
