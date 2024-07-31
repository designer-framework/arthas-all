package com.taobao.arthas.plugin.core.profiling.lifecycle;

import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.core.advisor.AbstractLifeCyclePointcutAdvisor;
import com.taobao.arthas.core.vo.AgentStatistics;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SpringApplicationLifeCyclePointcutAdvisor extends AbstractLifeCyclePointcutAdvisor {

    public SpringApplicationLifeCyclePointcutAdvisor(AgentLifeCycle agentLifeCycles, AgentStatistics agentStatistics) {
        super(agentLifeCycles, agentStatistics);
    }

}
