package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:58
 */
public interface StatisticsBuilder {

    Object build(SpringAgentStatistics springAgentStatistics);

    boolean support(String statisticsType);

}
