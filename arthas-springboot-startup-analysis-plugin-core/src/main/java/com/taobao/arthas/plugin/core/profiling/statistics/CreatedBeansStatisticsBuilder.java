package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.enums.StatisticsEnum;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:23
 */
public class CreatedBeansStatisticsBuilder implements StatisticsBuilder {

    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        return springAgentStatistics.getCreatedBeans();
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.beanInitResultList.getType().equals(statisticsType);
    }

}
