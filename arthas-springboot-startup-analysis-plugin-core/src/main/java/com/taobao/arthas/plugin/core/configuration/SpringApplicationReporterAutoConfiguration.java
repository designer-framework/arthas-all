package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.configuration.lifecycle.AgentLifeCycleAutoConfiguration;
import com.taobao.arthas.core.flamegraph.FlameGraph;
import com.taobao.arthas.core.properties.AgentOutputProperties;
import com.taobao.arthas.plugin.core.profiling.hook.StartReporterServerHook;
import com.taobao.arthas.plugin.core.profiling.hook.WriteStartUpAnalysisHtmlHook;
import com.taobao.arthas.plugin.core.properties.ArthasServerProperties;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ArthasServerProperties.class)
@AutoConfigureBefore(AgentLifeCycleAutoConfiguration.class)
public class SpringApplicationReporterAutoConfiguration {

    @Bean
    StartReporterServerHook startReporterServerHook(ArthasServerProperties arthasServerProperties) {
        return new StartReporterServerHook(arthasServerProperties);
    }

    @Bean
    ArthasServerProperties arthasServerProperties() {
        return new ArthasServerProperties();
    }

    @Bean
    WriteStartUpAnalysisHtmlHook writeStartUpAnalysisHtmlHook(
            ProfilingHtmlUtil profilingHtmlUtil
            , SpringAgentStatisticsVO springAgentStatisticsVO
            , FlameGraph flameGraph
    ) {
        return new WriteStartUpAnalysisHtmlHook(profilingHtmlUtil, springAgentStatisticsVO, flameGraph);
    }

    @Bean
    ProfilingHtmlUtil profilingHtmlUtil(AgentOutputProperties agentOutputProperties) {
        return new ProfilingHtmlUtil(agentOutputProperties);
    }

    @Bean
    SpringAgentStatisticsVO springAgentStatisticsVO() {
        return new SpringAgentStatisticsVO();
    }

}
