package com.taobao.arthas.plugin.core.lifecycle;

import com.taobao.arthas.core.properties.ArthasServerProperties;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import com.taobao.arthas.plugin.core.profiling.hook.StartReporterServerHook;
import com.taobao.arthas.plugin.core.profiling.hook.WriteStartUpAnalysisHtmlHook;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
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
    WriteStartUpAnalysisHtmlHook writeStartUpAnalysisHtmlHook(ProfilingHtmlUtil profilingHtmlUtil, ProfilingResultVO profilingResultVO) {
        return new WriteStartUpAnalysisHtmlHook(profilingHtmlUtil, profilingResultVO);
    }

}
