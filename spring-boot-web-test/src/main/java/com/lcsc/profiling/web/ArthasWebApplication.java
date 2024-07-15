package com.lcsc.profiling.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class ArthasWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArthasWebApplication.class, args);
    }

}
