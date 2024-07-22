package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.common.AnsiLog;
import com.taobao.arthas.core.server.ArthasBootstrap;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

public class ArthasEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String ARTHAS_HOME_PROPERTY = "arthas.home";

    private static String ARTHAS_HOME = null;

    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        /**
         * <pre>
         * 脚本里传过来的配置项，即命令行参数 > System Env > System Properties > arthas.properties
         * arthas.properties 提供一个配置项，可以反转优先级。 arthas.config.overrideAll=true
         * https://github.com/alibaba/arthas/issues/986
         * </pre>
         */
        loadArthasArgProperties(environment);
        loadArthasConfigurationProperties(environment);
    }

    private void loadArthasArgProperties(ConfigurableEnvironment environment) {

        Map<String, Object> copyMap = new HashMap<>();
        copyMap.put(ARTHAS_HOME_PROPERTY, arthasHome());

        MapPropertySource mapPropertySource = new MapPropertySource("ArthasArgsMapPropertySource", copyMap);
        environment.getPropertySources().addFirst(mapPropertySource);

    }

    private void loadArthasConfigurationProperties(ConfigurableEnvironment environment) throws IOException {

        String location = new File(arthasHome(), "arthas.properties").getAbsolutePath();

        if (new File(location).exists()) {

            PropertySource<?> propertySource = new PropertiesPropertySource(
                    location, PropertiesLoaderUtils.loadProperties(new FileSystemResource(location))
            );
            environment.getPropertySources().addFirst(propertySource);

        }

    }

    public String arthasHome() {
        CodeSource codeSource = ArthasBootstrap.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                ARTHAS_HOME = new File(codeSource.getLocation().toURI().getSchemeSpecificPart()).getParentFile().getAbsolutePath();
            } catch (Throwable e) {
                AnsiLog.error("try to find arthas.home from CodeSource error", e);
            }
        }
        if (ARTHAS_HOME == null) {
            ARTHAS_HOME = new File("").getAbsolutePath();
        }
        return ARTHAS_HOME;
    }

    @Override
    public int getOrder() {
        return SystemEnvironmentPropertySourceEnvironmentPostProcessor.DEFAULT_ORDER - 1;
    }

}
