package com.taobao.arthas.core.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AgentStatisticsVO implements AgentStatistics {

    protected final List<MethodInvokeVO> methodInvokes = new LinkedList<>();

    protected final Map<String, Integer> invokeStackTraceMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    protected volatile BigDecimal agentTime;

    @Override
    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    @Override
    public void addInvokeTrace(String stackTraceElements) {
        invokeStackTraceMap.put(stackTraceElements, invokeStackTraceMap.getOrDefault(stackTraceElements, 0) + 1);
    }

    @Override
    public Map<String, Integer> invokeStackTraceMap() {
        return invokeStackTraceMap;
    }

}
