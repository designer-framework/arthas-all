package com.taobao.arthas.plugin.core.configuration.trubo;

import com.taobao.arthas.plugin.core.configuration.SpringComponentMethodInvokeAutoConfiguration;
import com.taobao.arthas.plugin.core.properties.ComponentTurboProperties;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
        ApolloTurboConfiguration.class,
        SwaggerTurboConfiguration.class,
        FeignClientTurboConfiguration.class,
        ForkJoinTurboConfiguration.class
})
@EnableConfigurationProperties(value = ComponentTurboProperties.class)
@AutoConfigureBefore(SpringComponentMethodInvokeAutoConfiguration.class)
public class SpringComponentTurboAutoConfiguration {
}
