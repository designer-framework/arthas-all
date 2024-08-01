package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;

@Getter
@Setter
public abstract class BeanCreationEvent extends ApplicationEvent {

    private final String beanName;

    private BigDecimal startMillis;

    private BigDecimal endMillis;

    private BigDecimal duration;

    public BeanCreationEvent(Object source, String beanName) {
        super(source);
        this.beanName = beanName;
        startMillis = DurationUtils.nowMillis();
    }

    public void instantiated() {
        endMillis = DurationUtils.nowMillis();
        duration = endMillis.subtract(startMillis);
    }

}
