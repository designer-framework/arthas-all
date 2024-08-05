package com.taobao.arthas.core.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class AgentStatisticsVO implements AgentStatistics {

    protected final List<MethodInvokeVO> methodInvokes = new LinkedList<>();

    protected final Map<String, Integer> invokeStackTrace = new ConcurrentHashMap<>();

    @Setter
    protected volatile BigDecimal agentTime;

    @Override
    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    /**
     * 在线程运算或等待的时间里, 采样的数据会一致, 如果一致则累加1
     */
    @Override
    public void addInvokeTrace(String stackTraceElements) {
        invokeStackTrace.put(stackTraceElements, invokeStackTrace.getOrDefault(stackTraceElements, 0) + 1);
    }

    @Override
    public Map<String, Integer> getInvokeStackTrace() {
        return invokeStackTrace;
    }

}
