package com.taobao.arthas.spring.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class ProfilingResultVO {

    @Getter
    private final List<BeanCreateVO> createdBeans = new LinkedList<>();

    private final List<MethodInvokeVO> methodInvokes = new LinkedList<>();

    @Getter
    private final Map<String, Integer> invokeTraceMap = new ConcurrentHashMap<>();

    @Setter
    @Getter
    private long startUpTime;

    public void addCreatedBean(BeanCreateVO beanCreateVO) {
        createdBeans.add(beanCreateVO);
    }

    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    public void addInvokeTrace(String invokeTrace) {
        invokeTraceMap.put(invokeTrace, invokeTraceMap.getOrDefault(invokeTrace, 0) + 1);
    }

    public List<MethodInvokeMetrics> getMethodInvokeMetrics() {

        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        Map<String, List<MethodInvokeVO>> methodInvokesMap = methodInvokes.stream().collect(Collectors.groupingBy(MethodInvokeVO::getFullyQualifiedMethodName));

        for (Map.Entry<String, List<MethodInvokeVO>> methodInvokesEntry : methodInvokesMap.entrySet()) {

            //总耗时
            long totalCost = methodInvokesEntry.getValue().stream().mapToLong(MethodInvokeVO::getDuration).sum();
            //耗时占比
            double averageCost = totalCost / (1.0 * methodInvokesEntry.getValue().size());
            //耗时占比前100
            List<MethodInvokeVO> top100 = methodInvokesEntry.getValue().stream()
                    .sorted((o1, o2) -> (int) (o2.getDuration() - o1.getDuration())).limit(100).collect(Collectors.toList());

            metricsList.add(new MethodInvokeMetrics(methodInvokesEntry.getKey(), methodInvokesEntry.getValue().size(), totalCost, averageCost, top100));

        }

        return metricsList;
    }

}
