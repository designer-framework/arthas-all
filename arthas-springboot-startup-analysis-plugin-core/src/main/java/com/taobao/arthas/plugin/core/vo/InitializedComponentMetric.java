package com.taobao.arthas.plugin.core.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class InitializedComponentMetric {

    private String name;

    private BigDecimal value;

    private List<InitializedComponentMetric> children = new ArrayList<>();

}
