package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class InitializedComponentsMetric {

    @JSONField(name = "name")
    private String name;

    @JSONField(name = "value")
    private BigDecimal duration;

    private List<InitializedComponentsMetric> children = new ArrayList<>();

    /**
     * BeanCopy要用到的无参构造
     */
    public InitializedComponentsMetric() {
    }

    public InitializedComponentsMetric(String name, BigDecimal duration) {
        this.name = name;
        this.duration = duration;
    }

    public void addChildren(InitializedComponentsMetric initializedComponentsMetric) {
        children.add(initializedComponentsMetric);
    }

    public void addChildren(List<InitializedComponentsMetric> initializedComponentsMetrics) {
        children.addAll(initializedComponentsMetrics);
    }

    public void fillOthersDuration() {
        BigDecimal childCostTime = children.stream().map(InitializedComponentsMetric::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal otherCostTime = duration.subtract(childCostTime);
        if (BigDecimal.ZERO.compareTo(otherCostTime) != 0) {
            //其他耗时统计
            children.add(new InitializedComponentsMetric("Others", otherCostTime));
        }
    }

}
