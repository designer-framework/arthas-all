package com.taobao.arthas.spring;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingContainer;
import com.taobao.arthas.spring.constants.DisposableBeanOrdered;
import com.taobao.main.ProfilingApplication;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.arthas.SpyAPI;
import java.util.ArrayList;
import java.util.List;

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
     * @param args arthas.properties配置
     * @return
     */
    public static ConfigurableApplicationContext instance() {
        //继承arthas.properties的配置
        return ProfilingApplication.start(new String[0]);
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


}
