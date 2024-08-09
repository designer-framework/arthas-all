package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.plugin.core.enums.StatisticsEnum;
import com.taobao.arthas.plugin.core.enums.StatisticsType;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import org.springframework.core.Ordered;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 00:00
 */
public class SpringApplicationStatisticsAggregation implements StatisticsAggregation, Ordered {

    private final SpringAgentStatistics springAgentStatistics;

    private final List<StatisticsBuilder> statisticsBuilders;

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    private final Object NULL = new Object();

    public SpringApplicationStatisticsAggregation(SpringAgentStatistics springAgentStatistics, List<StatisticsBuilder> statisticsBuilders) {
        this.springAgentStatistics = springAgentStatistics;
        this.statisticsBuilders = statisticsBuilders;
    }

    public Map<String, Object> statisticsAggregation() {
        return Arrays.stream(StatisticsEnum.values()).parallel()
                .collect(Collectors.toMap(StatisticsEnum::getType, this::statistics));
    }

    @Override
    public Object statistics(StatisticsType statisticsType) {
        return cache.computeIfAbsent(statisticsType.getType(), type -> {

            //
            return statisticsBuilders.stream()
                    .filter(statisticsBuilder -> statisticsBuilder.support(type))
                    .map(statisticsBuilder -> statisticsBuilder.build(springAgentStatistics))
                    .findFirst()
                    .orElse(NULL);

        });
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.UPLOAD_STATISTICS;
    }

}
