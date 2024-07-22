package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.constants.ProfilingLifeCycleOrdered;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 01:49
 */
public class ArthasExtensionShutdownHookPostProcessor implements ApplicationContextAware, ProfilingLifeCycle, Ordered {

    @Setter
    private ApplicationContext applicationContext;

    @Override
    public void stop() {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).close();
        }
    }

    @Override
    public int getOrder() {
        return ProfilingLifeCycleOrdered.STOP_CONTAINER;
    }

}
