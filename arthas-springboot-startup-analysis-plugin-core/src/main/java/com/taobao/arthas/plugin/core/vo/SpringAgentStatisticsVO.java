package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.AgentStatisticsVO;
import com.taobao.arthas.core.vo.MethodInvokeVO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class SpringAgentStatisticsVO extends AgentStatisticsVO implements SpringAgentStatistics {

    private final Map<String, BeanCreateVO> createdBeansMap = new LinkedHashMap<>();

    private final List<InitializedComponent> initializedComponents = new LinkedList<>();

    @Override
    public void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer) {
        consumer.accept(createdBeansMap.get(beanName));
    }

    @Override
    public void addCreatedBean(BeanCreateVO beanCreateVO) {
        createdBeansMap.put(beanCreateVO.getName(), beanCreateVO);
    }

    @Override
    public void addInitializedComponent(InitializedComponent initializedComponent) {
        initializedComponents.add(initializedComponent);
    }

    public List<BeanCreateVO> getCreatedBeans() {
        return new ArrayList<>(createdBeansMap.values());
    }

    @Override
    @JSONField(name = "startUpTime")
    public BigDecimal getAgentTime() {
        return super.getAgentTime();
    }

    @JSONField(name = "beanInitResultList")
    public Collection<BeanCreateVO> getBeanInitResultList() {
        return createdBeansMap.values();
    }

    @JSONField(name = "initializedComponentDetailList")
    public InitializedComponentsMetric getInitializedComponentsMetrics() {
        BigDecimal totalCost = initializedComponents.stream().map(InitializedComponent::getDuration)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        InitializedComponentsMetric rootMetric = new InitializedComponentsMetric();
        rootMetric.setDuration(totalCost);
        rootMetric.setShowName("ApplicationStartUpTotalCost");

        List<InitializedComponentsMetric> children = rootMetric.getChildren();

        children.addAll(
                initializedComponents.stream()
                        .map(initializedComponent -> {
                            InitializedComponentsMetric componentMetric = new InitializedComponentsMetric();
                            componentMetric.setShowName(initializedComponent.getShowName());
                            componentMetric.setDuration(initializedComponent.getDuration());
                            return componentMetric;
                        }).collect(Collectors.toList())
        );
        rootMetric.setChildren(children);

        //todo 将AOP耗时统计进来
        return rootMetric;
    }

    @JSONField(name = "methodInvokeDetailList")
    public List<MethodInvokeMetrics> getMethodInvokeMetrics() {
        List<MethodInvokeMetrics> metricsList = new ArrayList<>();

        //全限定方法名分组
        Map<String, List<MethodInvokeVO>> methodInvokesMap = methodInvokes.stream().collect(Collectors.groupingBy(MethodInvokeVO::getMethodQualifier));

        //分析每组的耗时
        for (Map.Entry<String, List<MethodInvokeVO>> methodInvokesEntry : methodInvokesMap.entrySet()) {

            //总耗时
            BigDecimal totalCost = methodInvokesEntry.getValue().stream().map(MethodInvokeVO::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
            //耗时占比/ms
            BigDecimal averageCost = totalCost.divide(BigDecimal.valueOf(methodInvokesEntry.getValue().size()), 3, RoundingMode.HALF_UP);
            //耗时占比前100
            List<MethodInvokeVO> top100 = methodInvokesEntry.getValue().stream()
                    .sorted((o1, o2) -> o2.getDuration().compareTo(o1.getDuration())).limit(100).collect(Collectors.toList());

            metricsList.add(new MethodInvokeMetrics(
                    methodInvokesEntry.getKey(), methodInvokesEntry.getValue().size(), totalCost, averageCost, top100
            ));

        }
        return metricsList;
    }

}
