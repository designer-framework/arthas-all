package com.taobao.arthas.spring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:application.properties", name = "ArthasSpringExtensionProperties")
public class TracePropertiesConfiguration {
}
