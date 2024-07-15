package com.taobao.arthas.spring;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingAdaptor;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.configuration.ArthasExtensionSpringPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.MapPropertySource;

import java.arthas.SpyAPI;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class SpringProfilingAdaptor implements ProfilingAdaptor {

    private static final AtomicBoolean adaptorStarted = new AtomicBoolean();
    private static final List<Runnable> agentShutdownHooks = new ArrayList<>();
    private static SpyAPI.AbstractSpy abstractSpy;
    private static Collection<MatchCandidate> matchCandidates;

    static {
        /**
         *
         * 初始化容器.
         * 可以考虑将环境变量及相关配置注入容器
         */
        if (adaptorStarted.compareAndSet(false, true)) {

            AnnotationConfigApplicationContext springContainer = new AnnotationConfigApplicationContext();

            Map<String, Object> jmxMapPropertySource = new HashMap<>();
            jmxMapPropertySource.put("spring.liveBeansView.mbeanDomain", "arthas-profiling");
            springContainer.getEnvironment().getPropertySources()
                    .addFirst(new MapPropertySource("ArthasProfilingJmxPropertySource", jmxMapPropertySource));

            //指定类加载器
            springContainer.setClassLoader(SpringProfilingAdaptor.class.getClassLoader());
            //支持注解式自动注入
            springContainer.addBeanFactoryPostProcessor(new ArthasExtensionSpringPostProcessor(springContainer, agentShutdownHooks));
            //扫包
            springContainer.scan("com.taobao.arthas");
            springContainer.refresh();

            //提供给core包, 用于判断哪些类需要增强
            matchCandidates = springContainer.getBeansOfType(MatchCandidate.class).values();
            //提供给core包, 增强的类被调用时会触发埋点 --> 会调用AbstractSpy
            abstractSpy = springContainer.getBean(SpyAPI.AbstractSpy.class);

            //发布初始化事件
            springContainer.getBeansOfType(ProfilingLifeCycle.class).values().forEach(ProfilingLifeCycle::start);

        }

    }

    @Override
    public SpyAPI.AbstractSpy getSpyAPI() {
        return abstractSpy;
    }

    @Override
    public Collection<MatchCandidate> getMatchCandidates() {
        return matchCandidates;
    }

    @Override
    public void addShutdownHook(Runnable runnable) {
        agentShutdownHooks.add(runnable);
    }

}
