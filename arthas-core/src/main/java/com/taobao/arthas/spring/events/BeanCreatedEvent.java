package com.taobao.arthas.spring.events;

public class BeanCreatedEvent extends BeanCreationEvent {

    public BeanCreatedEvent(Object source) {
        super(source);
    }

}
