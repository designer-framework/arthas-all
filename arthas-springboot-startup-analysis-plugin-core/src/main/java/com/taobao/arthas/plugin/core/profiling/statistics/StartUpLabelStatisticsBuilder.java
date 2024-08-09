package com.taobao.arthas.plugin.core.profiling.statistics;

import com.taobao.arthas.plugin.core.enums.StatisticsEnum;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StartUpLabelVO;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.LinkedList;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:10
 */
public class StartUpLabelStatisticsBuilder implements StatisticsBuilder {

    @Override
    public Object build(SpringAgentStatistics springAgentStatistics) {
        List<StartUpLabelVO> startUpLabels = new LinkedList<>();
        //项目启动时间
        startUpLabels.add(new StartUpLabelVO(Ordered.HIGHEST_PRECEDENCE, "Start Up Time/ms", springAgentStatistics.getAgentTime()));
        //Bean总数量
        startUpLabels.add(new StartUpLabelVO(Ordered.HIGHEST_PRECEDENCE + 1, "Num of Bean", springAgentStatistics.getCreatedBeans().size()));

        //排序
        AnnotationAwareOrderComparator.sort(startUpLabels);

        return startUpLabels;
    }

    @Override
    public boolean support(String statisticsType) {
        return StatisticsEnum.statisticsList.getType().equals(statisticsType);
    }

}
