package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StatisticsInfo;
import org.springframework.core.Ordered;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 00:00
 */
public class SpringApplicationStatisticsAggregation implements StatisticsAggregation, Ordered {

    private final SpringAgentStatistics springAgentStatistics;

    private final List<StatisticsBuilder> statisticsBuilders;

    public SpringApplicationStatisticsAggregation(SpringAgentStatistics springAgentStatistics, List<StatisticsBuilder> statisticsBuilders) {
        this.springAgentStatistics = springAgentStatistics;
        this.statisticsBuilders = statisticsBuilders;
    }

    @Override
    public Map<String, Object> statisticsAggregation() {
        return statisticsBuilders.stream()
                .map(statisticsBuilder -> statisticsBuilder.build(springAgentStatistics))
                .collect(Collectors.toMap(StatisticsInfo::getKey, StatisticsInfo::getStatisticsVO));
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.UPLOAD_STATISTICS;
    }

}
