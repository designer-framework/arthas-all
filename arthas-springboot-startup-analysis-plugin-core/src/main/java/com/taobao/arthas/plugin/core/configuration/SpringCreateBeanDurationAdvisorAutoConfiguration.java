package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.configuration.advisor.AdvisorUtils;
import com.taobao.arthas.plugin.core.profiling.bean.SpringBeanCreationPointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class SpringCreateBeanDurationAdvisorAutoConfiguration {

    /**
     * @see org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    @Bean
    public SpringBeanCreationPointcutAdvisor springBeanCreationPointcutAdvisor(SpringAgentStatisticsVO springAgentStatisticsVO) {
        return AdvisorUtils.build(
                new SpringBeanCreationPointcutAdvisor(springAgentStatisticsVO)
                , "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])");
    }

}
