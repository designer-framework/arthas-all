package com.taobao.arthas.spring.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Configuration
@PropertySource(value = "classpath:application.properties", name = "ArthasSpringExtensionProperties")
public class ArthasProperties {

}
