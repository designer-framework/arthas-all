package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.plugin.core.turbo.ApolloTurboAutoConfiguration;
import com.taobao.arthas.plugin.core.turbo.FeignClientTurboAutoConfiguration;
import com.taobao.arthas.plugin.core.turbo.SwaggerTurboAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

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
@Import({
        ApolloTurboAutoConfiguration.class,
        SwaggerTurboAutoConfiguration.class,
        FeignClientTurboAutoConfiguration.class
})
public class SpringComponentTurboAutoConfiguration {
}
