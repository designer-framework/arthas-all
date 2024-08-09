package com.taobao.arthas.plugin.core.enums;

import lombok.Getter;

@Getter
public enum StatisticsEnum implements StatisticsType {

    componentsMetric,
    beanInitResultList,
    methodInvokeDetailList,
    unusedJarMap,
    statisticsList,
    ;

    @Override
    public String getType() {
        return name();
    }

}
