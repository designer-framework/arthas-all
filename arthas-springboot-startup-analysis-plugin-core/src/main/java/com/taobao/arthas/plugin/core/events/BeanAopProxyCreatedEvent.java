package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class BeanAopProxyCreatedEvent extends BeanCreationEvent {

    private final String proxiedClassName;

    public BeanAopProxyCreatedEvent(Object source, String beanName, String proxiedClassName, BigDecimal startMillis) {
        super(source, beanName);
        this.proxiedClassName = proxiedClassName;
        setStartMillis(startMillis);
        setEndMillis(DurationUtils.nowMillis());
        setDuration(getEndMillis().subtract(getStartMillis()));
    }

}
