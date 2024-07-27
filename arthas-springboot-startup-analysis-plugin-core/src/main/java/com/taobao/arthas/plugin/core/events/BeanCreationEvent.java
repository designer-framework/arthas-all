package com.taobao.arthas.plugin.core.events;

import org.springframework.context.ApplicationEvent;

public abstract class BeanCreationEvent extends ApplicationEvent {

    public BeanCreationEvent(Object source) {
        super(source);
    }

}
