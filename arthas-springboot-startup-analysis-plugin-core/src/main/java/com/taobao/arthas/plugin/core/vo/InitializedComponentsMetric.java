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

}
