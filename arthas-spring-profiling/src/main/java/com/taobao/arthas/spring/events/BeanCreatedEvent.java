package com.taobao.arthas.spring.events;

import org.springframework.context.ApplicationEvent;

public class BeanCreatedEvent extends ApplicationEvent {

    public BeanCreatedEvent(Object source) {
        super(source);
    }

}
