package com.taobao.arthas.spring;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingContainer;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

import java.arthas.SpyAPI;
import java.util.ArrayList;
import java.util.List;

@Component
public class SpringProfilingContainer implements ProfilingContainer {

    private static final List<Runnable> agentShutdownHooks = new ArrayList<>();

    private static final AnnotationConfigApplicationContext springContainer;

    static {
        //
        springContainer = new SpringProfilingAnnotationConfigApplicationContext(agentShutdownHooks);
        springContainer.scan("com.taobao.arthas");
        springContainer.refresh();

        //发布初始化事件
        springContainer.getBeansOfType(ProfilingLifeCycle.class).values().forEach(ProfilingLifeCycle::start);
    }

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

    public static SpringProfilingContainer instance() {
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
