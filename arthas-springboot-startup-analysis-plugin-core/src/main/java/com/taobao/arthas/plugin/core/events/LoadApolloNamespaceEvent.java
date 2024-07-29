package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LoadApolloNamespaceEvent extends ApplicationEvent {

    private final InitializedComponent.Children loadedNamespace;

    public LoadApolloNamespaceEvent(Object source, InitializedComponent.Children loadedNamespace) {
        super(source);
        this.loadedNamespace = loadedNamespace;
    }

}
