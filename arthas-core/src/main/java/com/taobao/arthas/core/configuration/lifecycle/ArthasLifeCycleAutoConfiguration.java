package com.taobao.arthas.core.configuration.lifecycle;

import com.taobao.arthas.core.lifecycle.LifeCycleHook;
import com.taobao.arthas.core.lifecycle.SimpleLifeCycle;
import com.taobao.arthas.core.profiling.hook.AnalysisStopLifeCycleHook;
import com.taobao.arthas.core.profiling.hook.FlameGraphLifeCycleHook;
import com.taobao.arthas.core.properties.ArthasThreadTraceProperties;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-24 23:29
 */
@Configuration(proxyBeanMethods = false)
public class ArthasLifeCycleAutoConfiguration {

    @Bean
    SimpleLifeCycle simpleLifeCycle(List<LifeCycleHook> lifeCycleHooks) {
        return new SimpleLifeCycle(lifeCycleHooks);
    }

    @Bean
    AnalysisStopLifeCycleHook arthasExtensionShutdownHook() {
        return new AnalysisStopLifeCycleHook();
    }

    @Bean
    FlameGraphLifeCycleHook flameGraphLifeCycleHook(ArthasThreadTraceProperties arthasThreadTraceProperties, ProfilingResultVO profilingResultVO) {
        return new FlameGraphLifeCycleHook(arthasThreadTraceProperties, profilingResultVO);
    }

}
