package com.lcsc.profiling.web;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@EnableApolloConfig
@EnableFeignClients
@EnableDiscoveryClient(autoRegister = false)
@EnableAspectJAutoProxy(proxyTargetClass = true)
@SpringBootApplication(scanBasePackages = {"com.lcsc"})
public class ArthasWebApplication {
    @ConditionalOnAvailableEndpoint
    public static void main(String[] args) {
        SpringApplication.run(ArthasWebApplication.class, args);
    }

}
