package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public abstract class BeanCreationLifeCycleEvent extends ApplicationEvent {

    private final String beanName;

    private final BeanLifeCycleDuration lifeCycleDurations;

    public BeanCreationLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        super(source);
        this.beanName = beanName;
        this.lifeCycleDurations = lifeCycleDurations;
    }

}