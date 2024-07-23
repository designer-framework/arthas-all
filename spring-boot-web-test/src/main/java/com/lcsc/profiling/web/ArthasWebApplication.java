package com.lcsc.profiling.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;


@Slf4j
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication
public class ArthasWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(ArthasWebApplication.class, args);
    }

}
