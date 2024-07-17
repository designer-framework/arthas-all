package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.vo.MethodInvokeVO;
import com.taobao.arthas.spring.vo.TraceMethodInfo;

import java.util.HashMap;
import java.util.Map;

public class SpringMethodInvokeAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private final Map<String, MethodInvokeVO> INVOKE_DETAIL_MAP = new HashMap<>();

    public SpringMethodInvokeAdviceHandler(TraceMethodInfo traceMethodInfo) {
        super(traceMethodInfo);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        MethodInvokeVO invokeDetail = new MethodInvokeVO(getTraceMethodInfo().getFullyQualifiedMethodName(), invokeVO.getParams());
        INVOKE_DETAIL_MAP.put(getInvokeKey(invokeVO), invokeDetail);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        if (INVOKE_DETAIL_MAP.containsKey(getInvokeKey(invokeVO))) {
            MethodInvokeVO invokeDetail = INVOKE_DETAIL_MAP.get(getInvokeKey(invokeVO));
            invokeDetail.setDuration(System.currentTimeMillis() - invokeDetail.getStartMillis());
        }
    }

    protected String getInvokeKey(InvokeVO invokeVO) {
        return invokeVO.getHeadInvokeId() + ":" + invokeVO.getInvokeId();
    }

}
