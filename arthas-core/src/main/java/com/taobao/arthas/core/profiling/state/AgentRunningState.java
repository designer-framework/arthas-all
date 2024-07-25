package com.taobao.arthas.core.profiling.state;

import com.taobao.arthas.api.state.AgentState;
import lombok.Getter;

@Getter
public class AgentRunningState implements AgentState {

    /**
     * -- GETTER --
     * 开始性能分析
     */
    private volatile boolean started;

    @Override
    public void started() {
        started = true;
    }

    /**
     * 性能分析完毕
     */
    public void stop() {
        started = false;
    }

    /*@Override
    public int getOrder() {
        return ProfilingLifeCycleOrdered.STARTING_PROFILING;
    }*/

}
