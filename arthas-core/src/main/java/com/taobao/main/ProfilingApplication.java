package com.taobao.main;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.DefaultResourceLoader;

public class ProfilingApplication {

    public static ConfigurableApplicationContext start(String[] args) {
        return new SpringApplication(new DefaultResourceLoader(ProfilingApplication.class.getClassLoader()), ProfilingApplication.class)
                .run(args);
    }

}
