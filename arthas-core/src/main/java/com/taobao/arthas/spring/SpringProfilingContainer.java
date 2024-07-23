package com.taobao.arthas.spring;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.processor.ProfilingContainer;
import com.taobao.arthas.spring.constants.DisposableBeanOrdered;
import com.taobao.arthas.spring.properties.ArthasConfigProperties;
import lombok.Getter;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;

import java.arthas.SpyAPI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@SpringBootApplication(scanBasePackages = "com.taobao.arthas")
public class SpringProfilingContainer implements ProfilingContainer, DisposableBean, Ordered {

    private static final List<Runnable> agentShutdownHooks = new ArrayList<>();

    /**
     * 增强的类被调用时会触发埋点 --> 会调用AbstractSpy
     */
    @Autowired
    private SpyAPI.AbstractSpy spyAPI;

    /**
     * 用于判断哪些类需要增强
     */
    @Autowired
    private List<MatchCandidate> matchCandidates;

    /**
     * 用于判断哪些类需要增强
     */
    @Autowired
    private ArthasConfigProperties arthasConfigProperties;

    /**
     * 环境变量透传到性能分析容器中
     *
     * @param argsMap jvm配置
     * @return
     */
    public static ConfigurableApplicationContext instance(Map<String, String> argsMap) {
        return SpringApplication.run(SpringProfilingContainer.class);
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
