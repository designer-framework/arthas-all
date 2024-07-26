package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class InstantiateSingletonOverEvent extends BeanCreationEvent {

    private final String beanName;

    private final BigDecimal startTime;

    private BigDecimal endTime;

    private BigDecimal costTime;

    public InstantiateSingletonOverEvent(Object source, String beanName) {
        super(source);
        this.beanName = beanName;
        startTime = DurationUtils.nowMillis();
    }

    public InstantiateSingletonOverEvent instantiated() {
        endTime = DurationUtils.nowMillis();
        costTime = endTime.subtract(startTime);
        return this;
    }

}
