package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.Getter;

@Getter
public class ComponentInitializedEvent extends ComponentEvent {

    private final InitializedComponent initializedComponent;

    public ComponentInitializedEvent(Object source, InitializedComponent initializedComponent) {
        super(source);
        this.initializedComponent = initializedComponent;
    }

}
