package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.enums.StatisticsType;

import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:43
 */
public interface StatisticsAggregation {

    Map<String, Object> statisticsAggregation();

    Object statistics(StatisticsType statisticsType);

}
