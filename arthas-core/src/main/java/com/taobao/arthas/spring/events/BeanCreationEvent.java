package com.taobao.arthas.spring.events;

public abstract class BeanCreationEvent extends ProfilingEvent {

    public BeanCreationEvent(Object source) {
        super(source);
    }

}
