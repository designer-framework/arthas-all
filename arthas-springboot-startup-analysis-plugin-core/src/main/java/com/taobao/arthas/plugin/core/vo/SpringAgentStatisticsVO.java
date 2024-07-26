package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.AgentStatisticsVO;
import com.taobao.arthas.core.vo.MethodInvokeVO;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SpringAgentStatisticsVO extends AgentStatisticsVO {

    private final Map<String, BeanCreateVO> createdBeansMap = new LinkedHashMap<>();

    public void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer) {
        consumer.accept(createdBeansMap.get(beanName));
    }

    public void addCreatedBean(BeanCreateVO beanCreateVO) {
        createdBeansMap.put(beanCreateVO.getName(), beanCreateVO);
    }

    public List<BeanCreateVO> getCreatedBeans() {
        return new ArrayList<>(createdBeansMap.values());
    }

    @Override
    @JSONField(name = "startUpTime")
    public long getAgentTime() {
        return super.getAgentTime();
    }

    @JSONField(name = "beanInitResultList")
    public Collection<BeanCreateVO> getBeanInitResultList() {
        return createdBeansMap.values();
    }

    @JSONField(name = "methodInvokeDetailList")
    public List<MethodInvokeMetrics> getMethodInvokeDetailList() {
        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        //全限定方法名分组
        Map<String, List<MethodInvokeVO>> methodInvokesMap = methodInvokes.stream().collect(Collectors.groupingBy(MethodInvokeVO::getMethodQualifier));

        //分析每组的耗时
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
