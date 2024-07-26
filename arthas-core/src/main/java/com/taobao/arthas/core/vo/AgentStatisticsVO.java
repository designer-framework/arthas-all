package com.taobao.arthas.core.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class AgentStatisticsVO implements AgentStatistics {

    protected final List<MethodInvokeVO> methodInvokes = new LinkedList<>();

    protected final Map<String, Integer> invokeStackTraceMap = new ConcurrentHashMap<>();

    @Getter
    @Setter
    protected volatile long agentTime;

    @Override
    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    @Override
    public void addInvokeTrace(StackTraceElement[] stackTraceElements) {
        List<StackTraceElement> stackTraceElementList = Arrays.asList(stackTraceElements);

        if (stackTraceElementList.stream().anyMatch(element -> element.getClassName().startsWith("com.taobao.arthas"))) {
            return;
        }

        Collections.reverse(stackTraceElementList);

        //栈帧转换成String, 便于垃圾回收
        String stackTraceElementsString = stackTraceElementList.stream()
                .map(element -> element.getClassName() + "." + element.getMethodName()).collect(Collectors.joining(";"));

        invokeStackTraceMap.put(stackTraceElementsString, invokeStackTraceMap.getOrDefault(stackTraceElementsString, 0) + 1);
    }

    @Override
    public Map<String, Integer> invokeStackTraceMap() {
        return invokeStackTraceMap;
    }

}
