package com.taobao.arthas.core.hook;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.LifeCycleHook;
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
@Setter
public class StopLifeCycleHook implements ApplicationContextAware, LifeCycleHook, Ordered {

    private ApplicationContext applicationContext;

    /**
     * 销毁Bean
     *
     * @see org.springframework.beans.factory.DisposableBean#destroy()
     */
    @Override
    public void stop() {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            ((ConfigurableApplicationContext) applicationContext).close();
        }
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.STOP_PROFILER_CONTAINER;
    }

}
