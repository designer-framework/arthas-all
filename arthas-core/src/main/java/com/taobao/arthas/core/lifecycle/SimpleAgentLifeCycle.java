package com.taobao.arthas.core.lifecycle;

import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.api.state.AgentState;

import java.util.List;

public class SimpleAgentLifeCycle implements AgentLifeCycle, AgentState {

    private final List<AgentLifeCycleHook> agentLifeCycleHooks;

    /**
     * -- GETTER --
     * 开始性能分析
     */
    private volatile boolean started;

    public SimpleAgentLifeCycle(List<AgentLifeCycleHook> agentLifeCycleHooks) {
        this.agentLifeCycleHooks = agentLifeCycleHooks;
    }

    @Override
    public void start() {
        agentLifeCycleHooks.forEach(AgentLifeCycleHook::start);
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        agentLifeCycleHooks.forEach(AgentLifeCycleHook::stop);
    }

    @Override
    public boolean isStarted() {
        return started;
    }

}
