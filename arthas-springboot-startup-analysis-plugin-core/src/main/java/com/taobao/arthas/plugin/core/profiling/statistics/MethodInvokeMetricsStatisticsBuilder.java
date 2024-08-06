package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.vo.MethodInvokeMetrics;
import com.taobao.arthas.plugin.core.vo.SimpleStatisticsInfo;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StatisticsInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:39
 */
public class MethodInvokeMetricsStatisticsBuilder implements StatisticsBuilder {

    @Override
    public StatisticsInfo build(SpringAgentStatistics springAgentStatistics) {
        return new SimpleStatisticsInfo("methodInvokeDetailList", getMethodInvokeMetrics(springAgentStatistics.getMethodInvokes()));
    }

    private List<MethodInvokeMetrics> getMethodInvokeMetrics(Collection<MethodInvokeVO> methodInvokes) {
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
