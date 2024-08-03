package com.taobao.arthas.plugin.core.enums;

import lombok.Getter;

@Getter
public enum SpringComponentEnum implements ComponentEnum {

    SPRING_APPLICATION("SPRING_APPLICATION", "SpringApplication"),
    ABSTRACT_AUTO_PROXY_CREATOR("ABSTRACT_AUTO_PROXY_CREATOR", "AbstractAutoProxyCreator"),
    CLASS_PATH_SCANNING_CANDIDATE("CLASS_PATH_SCANNING_CANDIDATE_COMPONENT_PROVIDER", "ClassPathScanningCandidateComponentProvider"),
    SMART_INITIALIZING_SINGLETON("SMART_INITIALIZING_SINGLETON", "SmartInitializingSingleton"),
    INIT_DESTROY_ANNOTATION_BEAN("INIT_DESTROY_ANNOTATION_BEAN", "InitDestroyAnnotationBean"),
    APOLLO_APPLICATION_CONTEXT_INITIALIZER("APOLLO_APPLICATION_CONTEXT_INITIALIZER", "ApolloApplicationContextInitializer"),
    FEIGN_CLIENT_FACTORY_BEAN("FEIGN_CLIENT_FACTORY_BEAN", "FeignClientFactoryBean"),
    SWAGGER("SWAGGER", "Swagger");

    private final String componentName;

    private final String showName;

    SpringComponentEnum(String componentName, String showName) {
        this.componentName = componentName;
        this.showName = showName;
    }

}
