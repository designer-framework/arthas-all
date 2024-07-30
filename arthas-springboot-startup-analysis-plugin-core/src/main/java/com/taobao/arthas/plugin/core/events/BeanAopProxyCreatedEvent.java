package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BeanAopProxyCreatedEvent extends BeanCreationEvent {

    private final BigDecimal startTime;

    private final BigDecimal endTime;

    private final BigDecimal costTime;

    private final String beanName;

    private final String proxiedClassName;

    public BeanAopProxyCreatedEvent(Object source, String beanName, String proxiedClassName, BigDecimal startTime) {
        super(source);
        this.beanName = beanName;
        this.proxiedClassName = proxiedClassName;
        this.startTime = startTime;
        endTime = DurationUtils.nowMillis();
        costTime = endTime.subtract(startTime);
    }

}
