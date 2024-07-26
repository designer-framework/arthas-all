package com.taobao.arthas.plugin.core.events;

public abstract class BeanCreationEvent extends ProfilingEvent {

    public BeanCreationEvent(Object source) {
        super(source);
    }

}
