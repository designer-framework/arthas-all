package com.taobao.arthas.plugin.core.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ComponentEvent extends ApplicationEvent {

    public ComponentEvent(Object source) {
        super(source);
    }

}