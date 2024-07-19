package com.taobao.arthas.spring;

import com.taobao.arthas.core.config.BinderUtils;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingContainer;
import com.taobao.arthas.spring.configuration.ArthasExtensionAnnotationConfigProcessor;
import com.taobao.arthas.spring.configuration.ArthasExtensionMethodInvokePostProcessor;
import com.taobao.arthas.spring.configuration.ArthasExtensionShutdownHookPostProcessor;
import com.taobao.arthas.spring.constants.DisposableBeanOrdered;
import com.taobao.arthas.spring.properties.ArthasProperties;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.LiveBeansView;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.arthas.SpyAPI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SpringProfilingContainer implements ProfilingContainer, DisposableBean, Ordered {

    private static final List<Runnable> agentShutdownHooks = new ArrayList<>();

    /**
     * 增强的类被调用时会触发埋点 --> 会调用AbstractSpy
     */
    @Autowired
    private SpyAPI.AbstractSpy abstractSpy;
    /**
     * 用于判断哪些类需要增强
     */
    @Autowired
    private List<MatchCandidate> matchCandidates;

    private SpringProfilingContainer() {
    }

    /**
     * 环境变量透传到性能分析容器中
     *
     * @param arthasEnvironment arthas.properties配置
     * @return
     */
    public static SpringProfilingContainer instance(StandardEnvironment arthasEnvironment) {
        //继承arthas.properties的配置
        AnnotationConfigApplicationContext springContainer = new SpringProfilingAnnotationConfigApplicationContext(arthasEnvironment);
        //指定扫包范围
        springContainer.scan("com.taobao.arthas");

        //
        ArthasProperties arthasProperties = new ArthasProperties();
        BinderUtils.inject(arthasEnvironment, arthasProperties);
        springContainer.addBeanFactoryPostProcessor(new ArthasExtensionMethodInvokePostProcessor(arthasProperties));
        //支持注解式自动注入
        springContainer.addBeanFactoryPostProcessor(new ArthasExtensionAnnotationConfigProcessor());
        //shutdown钩子
        springContainer.addBeanFactoryPostProcessor(new ArthasExtensionShutdownHookPostProcessor(springContainer));

        //刷新Spring上下文
        springContainer.refresh();

        return springContainer.getBean(SpringProfilingContainer.class);
    }

    @Override
    public SpyAPI.AbstractSpy getSpyAPI() {
        return abstractSpy;
    }

    @Override
    public List<MatchCandidate> getMatchCandidates() {
        return matchCandidates;
    }

    @Override
    public void addShutdownHook(Runnable runnable) {
        agentShutdownHooks.add(runnable);
    }

    @Override
    public void destroy() throws Exception {
        agentShutdownHooks.forEach(Runnable::run);
    }

    @Override
    public int getOrder() {
        return DisposableBeanOrdered.RELEASE_ARTHAS_AGENT;
    }

    private static class SpringProfilingAnnotationConfigApplicationContext extends AnnotationConfigApplicationContext {

        public SpringProfilingAnnotationConfigApplicationContext(ConfigurableEnvironment configurableEnvironment) {
            setEnvironment(configurableEnvironment);
            setClassLoader(this.getClass().getClassLoader());
        }

        @Override
        protected void prepareRefresh() {
            postProcessorEnvironment();
            super.prepareRefresh();
        }

        /**
         * IDEA默认打开Jmx, 会因为两个容器启动导致报错
         */
        private void postProcessorEnvironment() {
            //注入环境变量
            Map<String, Object> jmxMapPropertySource = new HashMap<>();
            jmxMapPropertySource.put(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, "arthas-profiling");
            ConfigurableEnvironment environment = getEnvironment();
            environment.getPropertySources()
                    .addFirst(new MapPropertySource("ArthasProfilingJmxPropertySource", jmxMapPropertySource));
        }

    }


}
