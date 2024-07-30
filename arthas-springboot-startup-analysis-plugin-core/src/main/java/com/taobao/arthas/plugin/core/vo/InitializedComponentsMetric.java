package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class InitializedComponentsMetric {

    @JSONField(name = "name")
    private String showName;

    @JSONField(name = "value")
    private BigDecimal duration;

    private List<InitializedComponentsMetric> children = new ArrayList<>();

    /**
     * BeanCopy要用到的无参构造
     */
    public InitializedComponentsMetric() {
    }

    public InitializedComponentsMetric(String showName, BigDecimal duration) {
        this.showName = showName;
        this.duration = duration;
    }

    public void addChildren(InitializedComponentsMetric initializedComponentsMetric) {
        children.add(initializedComponentsMetric);
    }

    public void addChildren(List<InitializedComponentsMetric> initializedComponentsMetrics) {
        children.addAll(initializedComponentsMetrics);
    }

    public InitializedComponentsMetric fillOthersDuration() {
        BigDecimal childCostTime = children.stream().map(InitializedComponentsMetric::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
        //其他耗时统计
        children.add(new InitializedComponentsMetric("Others", duration.subtract(childCostTime)));
        return this;
    }

}
