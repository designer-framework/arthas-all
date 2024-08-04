package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StatisticsInfo;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:58
 */
public interface StatisticsBuilder {

    StatisticsInfo build(SpringAgentStatistics springAgentStatistics);

}
