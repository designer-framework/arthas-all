package com.taobao.arthas.core.events;

public abstract class BeanCreationEvent extends ProfilingEvent {

    public BeanCreationEvent(Object source) {
        super(source);
    }

}
