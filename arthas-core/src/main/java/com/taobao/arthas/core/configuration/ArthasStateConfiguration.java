package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.profiling.state.AgentRunningState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArthasStateConfiguration {

    @Bean
    public AgentRunningState agentRunningState() {
        return new AgentRunningState();
    }

}
