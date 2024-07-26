package com.taobao.arthas.core.configuration.lifecycle;

import com.taobao.arthas.core.profiling.hook.StartReporterServerHook;
import com.taobao.arthas.core.profiling.hook.WriteStartUpAnalysisHtmlHook;
import com.taobao.arthas.core.properties.ArthasServerProperties;
import com.taobao.arthas.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
public class ArthasDestroyBeanAutoConfiguration {

    @Bean
    StartReporterServerHook startReporterServerHook(ArthasServerProperties arthasServerProperties, ProfilingHtmlUtil profilingHtmlUtil) {
        return new StartReporterServerHook(arthasServerProperties, profilingHtmlUtil);
    }

    @Bean
    WriteStartUpAnalysisHtmlHook startReporterServerHook(ProfilingHtmlUtil profilingHtmlUtil, ProfilingResultVO profilingResultVO) {
        return new WriteStartUpAnalysisHtmlHook(profilingHtmlUtil, profilingResultVO);
    }

}
