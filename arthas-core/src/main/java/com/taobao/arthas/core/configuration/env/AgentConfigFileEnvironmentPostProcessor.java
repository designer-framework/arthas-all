package com.taobao.arthas.core.configuration.env;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.config.ConfigFileApplicationListener;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class AgentConfigFileEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {

    private static final String PROFILING_JAR_HOME = "spring.profiling.output.home";

    /**
     * 比系统环境变量配置加载更早
     *
     * @return
     * @see ConfigFileApplicationListener
     * @see org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor
     */
    @Override
    public int getOrder() {
        return ConfigFileApplicationListener.DEFAULT_ORDER + 1;
    }

    /**
     * @param environment the environment to post-process
     * @param application the application to which the environment belongs
     * @see com.taobao.arthas.core.properties.AgentConfigProperties#getAllowOverridingDefaultProperties()
     */
    @Override
    @SneakyThrows
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        //从Agent目录读取application.yml配置文件
        loadArthasConfigurationProperties(environment, environment.getRequiredProperty(AgentHomeEnvironmentPostProcessor.PROFILING_JAR_HOME));
    }

    private void loadArthasConfigurationProperties(ConfigurableEnvironment environment, String agentHome) throws IOException {
        File location = new File(agentHome, "application.yml");

        if (location.exists()) {

            //解析配置文件
            List<PropertySource<?>> propertySourceList = new YamlPropertySourceLoader()
                    .load(location.getAbsolutePath(), new FileSystemResource(location));

            propertySourceList.forEach(propertySource -> {

                /**
                 * <pre>
                 * ${AgentHome}/application.yml > System Env > System Properties > classpath:/application.yml
                 * arthas.properties 提供一个配置项，可以反转优先级。 spring.agent.allow-overriding-default-properties=true
                 *
                 * </pre>
                 */
                String allowPropertiesOverriding = String.valueOf(propertySource.getProperty("spring.agent.allow-overriding-default-properties"));
                //允许重写默认配置
                if (Boolean.parseBoolean(allowPropertiesOverriding)) {
                    environment.getPropertySources().addLast(propertySource);
                    //不允许重写默认配置
                } else {
                    environment.getPropertySources().addFirst(propertySource);
                }

            });

        }
    }

}