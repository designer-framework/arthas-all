package com.taobao.arthas.core.configuration;

import com.taobao.arthas.core.ArthasBootstrap;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ArthasEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROFILING_JAR_HOME = "spring.profiling.home";

    /**
     * 比系统环境变量配置加载更早
     *
     * @return
     */
    @Override
    public int getOrder() {
        return SystemEnvironmentPropertySourceEnvironmentPostProcessor.DEFAULT_ORDER - 1;
    }

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
        loadArthasConfigurationProperties(environment);
    }

    private void loadArthasConfigurationProperties(ConfigurableEnvironment environment) throws IOException {

        Map<String, Object> copyMap = new HashMap<>();
        copyMap.put(PROFILING_JAR_HOME, profilingJarHome());

        MapPropertySource mapPropertySource = new MapPropertySource("ArthasArgsMapPropertySource", copyMap);
        environment.getPropertySources().addFirst(mapPropertySource);

        //解析配置文件
        String location = new File(profilingJarHome(), "application.yml").getAbsolutePath();

        if (new File(location).exists()) {

            new YamlPropertySourceLoader()
                    .load(location, new FileSystemResource(location))
                    .forEach(propertySource -> {

                        environment.getPropertySources().addFirst(propertySource);

                    });

        }

    }

    public String profilingJarHome() {
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
