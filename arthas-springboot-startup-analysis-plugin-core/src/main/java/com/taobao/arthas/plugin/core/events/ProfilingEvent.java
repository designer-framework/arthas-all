package com.taobao.arthas.plugin.core.events;

import org.springframework.context.ApplicationEvent;

public abstract class ProfilingEvent extends ApplicationEvent {

    public ProfilingEvent(Object source) {
        super(source);
    }

}
