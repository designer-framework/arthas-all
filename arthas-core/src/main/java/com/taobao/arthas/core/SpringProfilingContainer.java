package com.taobao.arthas.core;

import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.processor.ProfilingContainer;
import com.taobao.arthas.core.constants.DisposableBeanOrdered;
import com.taobao.arthas.core.properties.ArthasClassLoaderProperties;
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
    private List<PointcutAdvisor> pointcutAdvisor;

    /**
     * 用于判断哪些类需要增强
     */
    @Autowired
    private ArthasClassLoaderProperties arthasClassLoaderProperties;

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
    public List<PointcutAdvisor> getPointcutAdvisor() {
        return pointcutAdvisor;
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