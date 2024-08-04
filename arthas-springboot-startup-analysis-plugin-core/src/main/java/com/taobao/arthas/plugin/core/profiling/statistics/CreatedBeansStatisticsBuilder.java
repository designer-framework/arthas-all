package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.vo.SimpleStatisticsInfo;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StatisticsInfo;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:23
 */
public class CreatedBeansStatisticsBuilder implements StatisticsBuilder {

    @Override
    public StatisticsInfo build(SpringAgentStatistics springAgentStatistics) {
        return new SimpleStatisticsInfo("beanInitResultList", springAgentStatistics.getCreatedBeans());
    }

}
