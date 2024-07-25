package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.profiling.state.AgentState;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArthasStateConfiguration {

    @Bean
    public AgentState agentState() {
        return new AgentState();
    }

}
