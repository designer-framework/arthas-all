package com.taobao.arthas.plugin.core.advisor;

import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
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

    @Override
    public void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
    }

    @Override
    public void atExit(InvokeVO invokeVO) {
        super.atExit(invokeVO);
    }
    
}
