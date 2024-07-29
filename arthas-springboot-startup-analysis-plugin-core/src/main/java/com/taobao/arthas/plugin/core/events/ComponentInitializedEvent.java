package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ComponentInitializedEvent extends ApplicationEvent {

    private final InitializedComponent initializedComponent;

    public ComponentInitializedEvent(Object source, InitializedComponent initializedComponent) {
        super(source);
        this.initializedComponent = initializedComponent;
    }

}
