package com.taobao.arthas.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-17 00:40
 */
public class SpringProfilingAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {

    public SpringProfilingAnnotationConfigApplicationContext(ConfigurableEnvironment configurableEnvironment) {
        setEnvironment(configurableEnvironment);
    }

    @Override
    protected void prepareRefresh() {
        postProcessorEnvironment();
        super.prepareRefresh();
    }

    private void postProcessorEnvironment() {
        //注入环境变量
        Map<String, Object> jmxMapPropertySource = new HashMap<>();
        jmxMapPropertySource.put("spring.liveBeansView.mbeanDomain", "arthas-profiling");
        ConfigurableEnvironment environment = getEnvironment();
        environment.getPropertySources()
                .addFirst(new MapPropertySource("ArthasProfilingJmxPropertySource", jmxMapPropertySource));
    }

}
