package com.taobao.arthas.spring;

import com.taobao.arthas.core.config.BinderUtils;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingContainer;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.configuration.ArthasExtensionSpringPostProcessor;
import com.taobao.arthas.spring.properties.ArthasProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.stereotype.Component;

import java.arthas.SpyAPI;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpringProfilingContainer implements ProfilingContainer {

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
     * @param standardEnvironment
     * @return
     */
    public static SpringProfilingContainer instance(StandardEnvironment standardEnvironment) {
        //继承环境变量
        AnnotationConfigApplicationContext springContainer = new SpringProfilingAnnotationConfigApplicationContext(standardEnvironment);
        //指定类加载器
        springContainer.setClassLoader(SpringProfilingAnnotationConfigApplicationContext.class.getClassLoader());
        //指定扫包范围
        springContainer.scan("com.taobao.arthas");
        ArthasProperties arthasProperties = new ArthasProperties();
        BinderUtils.inject(standardEnvironment, arthasProperties);

        //支持注解式自动注入
        springContainer.addBeanFactoryPostProcessor(new ArthasExtensionSpringPostProcessor(springContainer, agentShutdownHooks));

        springContainer.refresh();

        //调用start
        springContainer.getBeansOfType(ProfilingLifeCycle.class).values().forEach(ProfilingLifeCycle::start);

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

}
