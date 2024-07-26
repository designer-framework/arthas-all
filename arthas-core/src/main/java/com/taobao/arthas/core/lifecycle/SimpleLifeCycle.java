package com.taobao.arthas.core.lifecycle;

import com.taobao.arthas.api.lifecycle.LifeCycle;
import com.taobao.arthas.api.state.AgentState;

import java.util.List;

public class SimpleLifeCycle implements LifeCycle, AgentState {

    private final List<LifeCycleHook> lifeCycleHooks;

    /**
     * -- GETTER --
     * 开始性能分析
     */
    private volatile boolean started;

    public SimpleLifeCycle(List<LifeCycleHook> lifeCycleHooks) {
        this.lifeCycleHooks = lifeCycleHooks;
    }

    @Override
    public void start() {
        lifeCycleHooks.forEach(LifeCycleHook::start);
        started = true;
    }

    @Override
    public void stop() {
        started = false;
        lifeCycleHooks.forEach(LifeCycleHook::stop);
    }

    @Override
    public boolean isStarted() {
        return started;
    }

}
