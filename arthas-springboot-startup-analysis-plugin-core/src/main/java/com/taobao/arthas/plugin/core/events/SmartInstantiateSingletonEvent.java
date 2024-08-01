package com.taobao.arthas.plugin.core.events;

import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class SmartInstantiateSingletonEvent extends BeanCreationEvent {

    public SmartInstantiateSingletonEvent(Object source, String beanName) {
        super(source, beanName);
    }

}
