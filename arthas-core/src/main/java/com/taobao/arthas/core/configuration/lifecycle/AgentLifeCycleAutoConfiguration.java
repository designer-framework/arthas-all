package com.taobao.arthas.core.configuration.lifecycle;

import com.taobao.arthas.api.state.AgentState;
import com.taobao.arthas.api.state.SimpleAgentState;
import com.taobao.arthas.core.hook.AgentLifeCycleStopHook;
import com.taobao.arthas.core.hook.FlameGraphAgentLifeCycleHook;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.lifecycle.SimpleAgentLifeCycle;
import com.taobao.arthas.core.properties.AgentFlameGraphProperties;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.core.vo.AgentStatisticsVO;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration
public class AgentLifeCycleAutoConfiguration {

    @Bean
    SimpleAgentState simpleAgentState() {
        return new SimpleAgentState();
    }

    @Bean
    SimpleAgentLifeCycle simpleAgentLifeCycle(List<AgentLifeCycleHook> agentLifeCycleHooks, AgentState agentState) {
        return new SimpleAgentLifeCycle(agentLifeCycleHooks, agentState);
    }

    @Bean
    AgentLifeCycleStopHook agentLifeCycleStopHook() {
        return new AgentLifeCycleStopHook();
    }

    @Bean
    @ConditionalOnMissingBean
    AgentStatisticsVO agentStatisticsVO() {
        return new AgentStatisticsVO();
    }

    @Bean
    FlameGraphAgentLifeCycleHook flameGraphAgentLifeCycleHook(AgentFlameGraphProperties agentFlameGraphProperties, AgentStatistics agentStatistics) {
        return new FlameGraphAgentLifeCycleHook(agentFlameGraphProperties, agentStatistics);
    }

}
