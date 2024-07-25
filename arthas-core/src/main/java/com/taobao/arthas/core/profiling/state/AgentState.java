package com.taobao.arthas.core.profiling.state;

import com.taobao.arthas.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.core.constants.ProfilingLifeCycleOrdered;
import lombok.Getter;
import org.springframework.core.Ordered;

@Getter
public class AgentState implements ProfilingLifeCycle, Ordered {

    private volatile boolean started;

    @Override
    public void start() {
        started = true;
    }

    @Override
    public void stop() {
    }

    @Override
    public int getOrder() {
        return ProfilingLifeCycleOrdered.STARTING_PROFILING;
    }

}
