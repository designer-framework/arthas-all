package com.taobao.arthas.plugin.core.configuration.trubo;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;

public interface ApplicationTurboLifeCycleHook extends AgentLifeCycleHook {

    @Override
    default int getOrder() {
        return LifeCycleOrdered.AGENT_RETRANSFORM + 1;
    }

}
