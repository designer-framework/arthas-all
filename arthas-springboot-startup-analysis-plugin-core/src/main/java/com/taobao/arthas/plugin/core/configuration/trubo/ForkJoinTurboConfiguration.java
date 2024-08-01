package com.taobao.arthas.plugin.core.configuration.trubo;


import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.taobao.arthas.plugin.core.profiling.hook.AgentForkJoinPoolHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "fork-join")
public class ForkJoinTurboConfiguration {

    @Bean
    AgentForkJoinPoolHook agentForkJoinPoolHook() {
        return new AgentForkJoinPoolHook();
    }

}
