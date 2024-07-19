package com.taobao.arthas.spring.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Component
public class ProfilingResultVO {

    private final Map<String, BeanCreateVO> createdBeansMap = new LinkedHashMap<>();

    private final List<MethodInvokeVO> methodInvokes = new LinkedList<>();

    @Getter
    private final Map<String, Integer> invokeTraceMap = new ConcurrentHashMap<>();

    @Setter
    private long startUpTime;

    public void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer) {
        consumer.accept(createdBeansMap.get(beanName));
    }

    public void addCreatedBean(BeanCreateVO beanCreateVO) {
        createdBeansMap.put(beanCreateVO.getName(), beanCreateVO);
    }

    public List<BeanCreateVO> getCreatedBeans() {
        return new ArrayList<>(createdBeansMap.values());
    }

    public void addMethodInvoke(MethodInvokeVO methodInvokeVO) {
        methodInvokes.add(methodInvokeVO);
    }

    public void addInvokeTrace(String invokeTrace) {
        invokeTraceMap.put(invokeTrace, invokeTraceMap.getOrDefault(invokeTrace, 0) + 1);
    }

    @JSONField(name = "beanInitResultList")
    public Collection<BeanCreateVO> getBeanInitResultList() {
        return createdBeansMap.values();
    }

    @JSONField(name = "methodInvokeDetailList")
    public List<MethodInvokeMetrics> getMethodInvokeDetailList() {

        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        Map<String, List<MethodInvokeVO>> methodInvokesMap = methodInvokes.stream().collect(Collectors.groupingBy(MethodInvokeVO::getMethodQualifier));

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
