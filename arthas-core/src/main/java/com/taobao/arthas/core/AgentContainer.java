package com.taobao.arthas.core;

import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.context.ProfilingContainer;
import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.core.properties.AgentClassLoaderProperties;
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
public class AgentContainer implements ProfilingContainer, DisposableBean, Ordered {

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
    private AgentClassLoaderProperties agentClassLoaderProperties;

    /**
     * 环境变量透传到性能分析容器中
     *
     * @param argsMap jvm配置
     * @return
     */
    public static ConfigurableApplicationContext instance(Map<String, String> argsMap) {
        if (argsMap != null) {
            return SpringApplication.run(AgentContainer.class, getCommandLineArgs(argsMap));
        } else {
            return SpringApplication.run(AgentContainer.class);
        }
    }

    /**
     * @param argsMap
     * @return
     * @see org.springframework.core.env.SimpleCommandLinePropertySource
     */
    private static String[] getCommandLineArgs(Map<String, String> argsMap) {
        if (argsMap == null) {

            return new String[0];

        } else {

            //转换成SpringBoot能识别的环境变量格式
            return argsMap.entrySet().stream()
                    .map(entry -> "--" + entry.getKey() + "=" + entry.getValue())
                    .toArray(String[]::new);

        }
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
        return LifeCycleStopHookOrdered.RELEASE_ARTHAS_AGENT;
    }


}
