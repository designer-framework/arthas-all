package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.core.configuration.advisor.AdvisorUtils;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.plugin.core.advisor.SpringApplicationLifeCyclePointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringApplicationAdvisorAutoConfiguration {

    @Bean
    SpringApplicationLifeCyclePointcutAdvisor springApplicationLifeCyclePointcutAdvisor(AgentLifeCycle agentLifeCycles, AgentStatistics agentStatistics) {
        return AdvisorUtils.build(
                new SpringApplicationLifeCyclePointcutAdvisor(agentLifeCycles, agentStatistics)
                , "org.springframework.boot.SpringApplication#run(java.lang.Class, java.lang.String[])"
        );
    }

}
