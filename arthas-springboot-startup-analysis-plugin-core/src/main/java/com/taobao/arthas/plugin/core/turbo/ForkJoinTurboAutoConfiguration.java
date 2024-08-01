package com.taobao.arthas.plugin.core.turbo;


import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.taobao.arthas.plugin.core.profiling.hook.AgentForkJoinPoolHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "fork-join")
public class ForkJoinTurboAutoConfiguration {

    @Bean
    AgentForkJoinPoolHook agentForkJoinPoolHook() {
        return new AgentForkJoinPoolHook();
    }

}
