package com.taobao.arthas.plugin.core.lifecycle;

import com.taobao.arthas.core.properties.ArthasThreadTraceProperties;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import com.taobao.arthas.plugin.core.profiling.hook.FlameGraphLifeCycleHook;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
public class ArthasLifeCycleAutoConfiguration {

    @Bean
    FlameGraphLifeCycleHook flameGraphLifeCycleHook(ArthasThreadTraceProperties arthasThreadTraceProperties, ProfilingResultVO profilingResultVO) {
        return new FlameGraphLifeCycleHook(arthasThreadTraceProperties, profilingResultVO);
    }

}
