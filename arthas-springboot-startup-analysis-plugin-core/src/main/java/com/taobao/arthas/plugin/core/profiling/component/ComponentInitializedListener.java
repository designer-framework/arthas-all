package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import org.springframework.context.ApplicationListener;

public class ComponentInitializedListener implements ApplicationListener<ComponentInitializedEvent> {

    private final SpringAgentStatistics springAgentStatistics;

    public ComponentInitializedListener(SpringAgentStatistics springAgentStatistics) {
        this.springAgentStatistics = springAgentStatistics;
    }

    /**
     * @param componentInitializedEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(ComponentInitializedEvent componentInitializedEvent) {
        springAgentStatistics.addInitializedComponent(componentInitializedEvent.getInitializedComponent());
    }

}
