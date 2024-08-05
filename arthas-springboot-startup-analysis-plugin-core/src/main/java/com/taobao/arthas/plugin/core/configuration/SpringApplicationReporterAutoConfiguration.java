package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.configuration.lifecycle.AgentLifeCycleAutoConfiguration;
import com.taobao.arthas.core.flamegraph.FlameGraph;
import com.taobao.arthas.core.properties.AgentOutputProperties;
import com.taobao.arthas.plugin.core.profiling.hook.StartReporterServerHook;
import com.taobao.arthas.plugin.core.profiling.hook.WriteStartUpAnalysisHtmlHook;
import com.taobao.arthas.plugin.core.profiling.statistics.*;
import com.taobao.arthas.plugin.core.properties.AgentServerProperties;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.instrument.Instrumentation;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(AgentServerProperties.class)
@AutoConfigureBefore(AgentLifeCycleAutoConfiguration.class)
public class SpringApplicationReporterAutoConfiguration {

    @Bean
    StartReporterServerHook startReporterServerHook(AgentServerProperties agentServerProperties) {
        return new StartReporterServerHook(agentServerProperties);
    }

    @Bean
    WriteStartUpAnalysisHtmlHook writeStartUpAnalysisHtmlHook(
            ProfilingHtmlUtil profilingHtmlUtil
            , StatisticsAggregation statisticsAggregation
            , FlameGraph flameGraph
    ) {
        return new WriteStartUpAnalysisHtmlHook(profilingHtmlUtil, statisticsAggregation, flameGraph);
    }

    @Bean
    ProfilingHtmlUtil profilingHtmlUtil(AgentOutputProperties agentOutputProperties) {
        return new ProfilingHtmlUtil(agentOutputProperties);
    }

    @Bean
    SpringApplicationStatisticsAggregation springApplicationStatisticsAggregation(SpringAgentStatistics springAgentStatistics, List<StatisticsBuilder> statisticsBuilders) {
        return new SpringApplicationStatisticsAggregation(springAgentStatistics, statisticsBuilders);
    }

    @Bean
    SpringAgentStatistics springAgentStatisticsVO() {
        return new SpringAgentStatisticsVO();
    }

    @Bean
    StatisticsBuilder startUpLabelStatisticsBuilder() {
        return new StartUpLabelStatisticsBuilder();
    }

    @Bean
    StatisticsBuilder createdBeansStatisticsBuilder() {
        return new CreatedBeansStatisticsBuilder();
    }

    @Bean
    StatisticsBuilder methodInvokeMetricsStatisticsBuilder() {
        return new MethodInvokeMetricsStatisticsBuilder();
    }

    @Bean
    StatisticsBuilder classLoaderLoadJarStatisticsBuilder(Instrumentation instrumentation, SpringAgentStatistics springAgentStatistics) {
        return new ClassLoaderLoadJarStatisticsBuilder(instrumentation);
    }

    @Bean
    StatisticsBuilder componentsMetricStatisticsBuilder() {
        return new ComponentsMetricStatisticsBuilder();
    }

}
