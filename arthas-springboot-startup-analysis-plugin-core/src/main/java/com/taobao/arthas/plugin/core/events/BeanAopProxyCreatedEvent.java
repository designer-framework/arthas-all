package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;

import java.math.BigDecimal;

public class BeanAopProxyCreatedEvent extends BeanCreationEvent {

    private BigDecimal startTime;

    private BigDecimal endTime;

    private BigDecimal costTime;

    private String beanName;

    public BeanAopProxyCreatedEvent(Object source, String beanName, BigDecimal startTime) {
        super(source);
        this.beanName = beanName;
        this.startTime = startTime;
        endTime = DurationUtils.nowMillis();
        costTime = endTime.subtract(startTime);
    }

    public BigDecimal getCostTime() {
        return costTime;
    }

    public BigDecimal getStartTime() {
        return startTime;
    }

    public BigDecimal getEndTime() {
        return endTime;
    }

    public String getBeanName() {
        return beanName;
    }

}
