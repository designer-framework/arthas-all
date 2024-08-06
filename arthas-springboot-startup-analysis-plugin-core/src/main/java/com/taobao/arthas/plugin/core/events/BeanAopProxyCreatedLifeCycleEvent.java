package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;

@Getter
public class BeanAopProxyCreatedLifeCycleEvent extends BeanCreationLifeCycleEvent {

    public BeanAopProxyCreatedLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        super(source, beanName, lifeCycleDurations);
    }

}
