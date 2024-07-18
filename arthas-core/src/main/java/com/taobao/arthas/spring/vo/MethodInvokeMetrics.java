package com.taobao.arthas.spring.vo;

import lombok.Data;

import java.util.List;

/**
 * @author linyimin
 **/
@Data
public class MethodInvokeMetrics {

    private final String method;

    private final long invokeCount;

    private final long totalCost;

    private final String averageCost;

    private final List<MethodInvokeVO> invokeDetails;

    public MethodInvokeMetrics(String method, long invokeCount, long totalCost, double averageCost, List<MethodInvokeVO> invokeDetails) {
        this.method = method;
        this.invokeCount = invokeCount;
        this.totalCost = totalCost;
        this.averageCost = String.format("%.2f", averageCost);
        this.invokeDetails = invokeDetails;
    }

}
