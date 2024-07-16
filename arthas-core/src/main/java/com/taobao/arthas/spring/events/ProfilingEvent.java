package com.taobao.arthas.spring.events;

import org.springframework.context.ApplicationEvent;

public abstract class ProfilingEvent extends ApplicationEvent {

    public ProfilingEvent(Object source) {
        super(source);
    }

}
