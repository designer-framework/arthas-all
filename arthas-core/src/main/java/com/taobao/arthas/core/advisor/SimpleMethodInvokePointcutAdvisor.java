package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.constants.DisposableBeanOrdered;
import com.taobao.arthas.core.profiling.AbstractMethodMatchInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 23:00
 */
@Slf4j
public class SimpleMethodInvokePointcutAdvisor extends AbstractMethodMatchInvokePointcutAdvisor implements DisposableBean, Ordered {

    private final ThreadLocal<Map<String, MethodInvokeVO>> methodInvokeMapThreadLocal = ThreadLocal.withInitial(HashMap::new);

    @Autowired
    private ProfilingResultVO profilingResultVO;

    public SimpleMethodInvokePointcutAdvisor(String fullyQualifiedMethodName) {
        super(fullyQualifiedMethodName);
    }

    public SimpleMethodInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {

        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(classMethodInfo.getFullyQualifiedMethodName(), invokeVO.getParams());
        methodInvokeMapThreadLocal.get().put(getInvokeKey(invokeVO), methodInvokeVO);

        profilingResultVO.addMethodInvoke(methodInvokeVO);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        Map<String, MethodInvokeVO> methodInvokeMap = methodInvokeMapThreadLocal.get();
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            MethodInvokeVO invokeDetail = methodInvokeMap.get(getInvokeKey(invokeVO));
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());
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
        return DisposableBeanOrdered.RELEASE_METHOD_INVOKE;
    }

}
