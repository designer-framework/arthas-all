package com.taobao.arthas.core.configuration.lifecycle;

import com.taobao.arthas.core.hook.StopLifeCycleHook;
import com.taobao.arthas.core.lifecycle.LifeCycleHook;
import com.taobao.arthas.core.lifecycle.SimpleLifeCycle;
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
    StopLifeCycleHook analysisStopLifeCycleHook() {
        return new StopLifeCycleHook();
    }

}
