package com.taobao.arthas.spring;

import com.taobao.arthas.spring.configuration.ArthasExtensionSpringPostProcessor;
import com.taobao.arthas.spring.utils.AgentHomeUtil;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-17 00:40
 */
public class SpringProfilingAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {

    private final List<Runnable> agentShutdownHooks;

    public SpringProfilingAnnotationConfigApplicationContext(List<Runnable> agentShutdownHooks) {
        super();
        //指定类加载器
        setClassLoader(SpringProfilingAnnotationConfigApplicationContext.class.getClassLoader());
        this.agentShutdownHooks = agentShutdownHooks;
    }

    @Override
    protected void prepareRefresh() {
        postProcessorEnvironment();
        //支持注解式自动注入
        addBeanFactoryPostProcessor(new ArthasExtensionSpringPostProcessor(this, agentShutdownHooks));
        super.prepareRefresh();
    }

    private void postProcessorEnvironment() {
        //注入环境变量
        Map<String, Object> jmxMapPropertySource = new HashMap<>();
        jmxMapPropertySource.put("spring.liveBeansView.mbeanDomain", "arthas-profiling");
        ConfigurableEnvironment environment = getEnvironment();
        environment.getPropertySources()
                .addFirst(new MapPropertySource("ArthasProfilingJmxPropertySource", jmxMapPropertySource));
        //注入环境变量
        environment.getPropertySources()
                .addFirst(new PropertiesPropertySource("SpringContainerPropertySource", AgentHomeUtil.loadProperties("arthas.properties")));
    }

}
