package com.taobao.arthas.core.configuration.env;

import com.taobao.arthas.core.ArthasBootstrap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;

import java.io.File;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class AgentHomeEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String PROFILING_JAR_HOME = "spring.profiling.output.home";
    private static final String DEFAULT_PROPERTIES = "defaultProperties";

    /**
     * 比系统环境变量配置加载更早
     *
     * @return
     * @see org.springframework.boot.context.config.ConfigFileApplicationListener
     * @see org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER - 1;
    }

    /**
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     * @see com.taobao.arthas.core.properties.AgentConfigProperties#getAllowOverridingDefaultProperties()
     */
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //Agent所在的文件夹
        loadArthasAgentHome(environment, agentHome());

        Properties properties = new Properties();
        properties.put("spring.agent.flame-graph.high-precision", "true");
        environment.getPropertySources().addLast(new PropertiesPropertySource(DEFAULT_PROPERTIES, properties));
    }

    private void loadArthasAgentHome(ConfigurableEnvironment environment, String agentHome) {
        if (StringUtils.isNotBlank(agentHome)) {

            Map<String, Object> copyMap = new HashMap<>();
            copyMap.put(PROFILING_JAR_HOME, agentHome);

            MapPropertySource mapPropertySource = new MapPropertySource("ArthasArgsMapPropertySource", copyMap);
            environment.getPropertySources().addFirst(mapPropertySource);

        }
    }

    private String agentHome() {
        CodeSource codeSource = ArthasBootstrap.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                return new File(codeSource.getLocation().toURI().getSchemeSpecificPart()).getParentFile().getAbsolutePath();
            } catch (Throwable e) {
                log.error("try to find arthas.home from CodeSource error", e);
            }
        }
        return "";
    }

}
