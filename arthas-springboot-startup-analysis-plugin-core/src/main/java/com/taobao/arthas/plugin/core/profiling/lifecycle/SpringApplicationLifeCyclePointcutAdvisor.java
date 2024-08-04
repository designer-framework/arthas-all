package com.taobao.arthas.plugin.core.profiling.lifecycle;

import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.AbstractLifeCyclePointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SpringApplicationLifeCyclePointcutAdvisor extends AbstractLifeCyclePointcutAdvisor {

    public SpringApplicationLifeCyclePointcutAdvisor(AgentLifeCycle agentLifeCycles, SpringAgentStatistics springAgentStatistics) {
        super(agentLifeCycles, springAgentStatistics);
    }

    @Override
    public void atExit(InvokeVO invokeVO) {
        super.atExit(invokeVO);
        //用户的SpringBoot项目实例
        //springAgentStatistics.setApplicationContext(invokeVO.getReturnObj());
    }

}
