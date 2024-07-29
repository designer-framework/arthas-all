package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import com.taobao.arthas.plugin.core.turbo.FeignClientsCreatorTurbo;
import com.taobao.arthas.plugin.core.turbo.SwaggerTurbo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * list.{ ?#this == 99999}
 *
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 * @see com.taobao.arthas.plugin.core.condition.OnTurboCondition
 * @see com.taobao.arthas.plugin.core.properties.ComponentTurboProperties
 */
@Configuration(proxyBeanMethods = false)
public class SpringComponentTurboAutoConfiguration {

    @Bean
    @ConditionalOnTurboPropCondition(pluginName = "swagger")
    public SwaggerTurbo swaggerTurbo() {
        return new SwaggerTurbo();
    }

    @Bean
    @ConditionalOnTurboPropCondition(pluginName = "open-feign")
    public FeignClientsCreatorTurbo feignClientsCreatorTurbo() {
        return new FeignClientsCreatorTurbo();
    }

}
