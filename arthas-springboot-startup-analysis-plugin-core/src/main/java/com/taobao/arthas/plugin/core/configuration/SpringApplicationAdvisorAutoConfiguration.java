package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.api.lifecycle.LifeCycle;
import com.taobao.arthas.core.advisor.SpringApplicationLifeCyclePointcutAdvisor;
import com.taobao.arthas.core.configuration.advisor.AdvisorUtils;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration(proxyBeanMethods = false)
public class SpringApplicationAdvisorAutoConfiguration {

    @Bean
    SpringApplicationLifeCyclePointcutAdvisor springApplicationLifeCyclePointcutAdvisor(List<LifeCycle> lifeCycles, ProfilingResultVO profilingResultVO) {
        return AdvisorUtils.build(
                new SpringApplicationLifeCyclePointcutAdvisor(lifeCycles, profilingResultVO)
                , "org.springframework.boot.SpringApplication#run(java.lang.Class, java.lang.String[])"
        );
    }

}
