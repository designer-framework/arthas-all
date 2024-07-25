package com.taobao.arthas.core.profiling.state;

import com.taobao.arthas.core.constants.ProfilingLifeCycleOrdered;
import lombok.Getter;
import org.springframework.core.Ordered;

@Getter
public class AgentState implements Ordered {

    private volatile boolean started;

    public void start() {
        started = true;
    }

    public void stop() {
        started = false;
    }

    @Override
    public int getOrder() {
        return ProfilingLifeCycleOrdered.STARTING_PROFILING;
    }

}
