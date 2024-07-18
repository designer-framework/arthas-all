package com.taobao.arthas.spring.profiling.invoke;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.profiling.AbstractInvokeAdviceHandler;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
import com.taobao.arthas.spring.vo.MethodInvokeVO;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link com.taobao.arthas.spring.configuration.ArthasExtensionMethodInvokePostProcessor}
 */
public class SpringMethodInvokeAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private final Map<String, MethodInvokeVO> methodInvokeMap = new HashMap<>();

    @Autowired
    private ProfilingResultVO profilingResultVO;

    public SpringMethodInvokeAdviceHandler(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        MethodInvokeVO methodInvokeVO = new MethodInvokeVO(getClassMethodInfo().getFullyQualifiedMethodName(), invokeVO.getParams());
        methodInvokeMap.put(getInvokeKey(invokeVO), methodInvokeVO);

        profilingResultVO.addMethodInvoke(methodInvokeVO);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        if (methodInvokeMap.containsKey(getInvokeKey(invokeVO))) {
            MethodInvokeVO invokeDetail = methodInvokeMap.get(getInvokeKey(invokeVO));
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());
        }
    }

    protected String getInvokeKey(InvokeVO invokeVO) {
        return invokeVO.getHeadInvokeId() + ":" + invokeVO.getInvokeId();
    }

}
