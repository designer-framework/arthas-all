package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.BeanLifeCycleDuration;
import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class SmartInstantiateSingletonLifeCycleEvent extends BeanCreationLifeCycleEvent {

    public SmartInstantiateSingletonLifeCycleEvent(Object source, String beanName, BeanLifeCycleDuration lifeCycleDurations) {
        super(source, beanName, lifeCycleDurations);
    }

}
