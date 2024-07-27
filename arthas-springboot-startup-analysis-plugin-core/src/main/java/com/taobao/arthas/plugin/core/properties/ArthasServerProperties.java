package com.taobao.arthas.plugin.core.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "server")
public class ArthasServerProperties {

    private Integer port;

}
