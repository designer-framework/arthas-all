package com.taobao.arthas.plugin.core.turbo;


import com.taobao.arthas.plugin.core.annotation.ConditionalOnTurboPropCondition;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnTurboPropCondition(pluginName = "open-feign")
public class FeignClientTurboAutoConfiguration {
}