package com.taobao.arthas.plugin.core.vo;

import lombok.AllArgsConstructor;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:50
 */
@AllArgsConstructor
public class SimpleStatisticsInfo implements StatisticsInfo {

    private String key;

    private Object statisticsVO;

    @Override
    public String getKey() {
        return key;
    }

    @Override
    public Object getStatisticsVO() {
        return statisticsVO;
    }

}
