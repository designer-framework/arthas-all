package com.taobao.arthas.plugin.core.enums;

import lombok.Getter;

@Getter
public enum SpringComponentEnum implements ComponentEnum {

    SPRING_APPLICATION("SPRING_APPLICATION", "SpringApplication"),
    ABSTRACT_AUTO_PROXY_CREATOR("ABSTRACT_AUTO_PROXY_CREATOR", "AbstractAutoProxyCreator"),
    CLASS_PATH_SCANNING("CLASS_PATH_SCANNING", "ClassPathScanning"),
    SMART_INITIALIZING_BEAN("SMART_INITIALIZING_BEAN", "SmartInitializingBean"),
    INIT_ANNOTATION_BEAN("INIT_ANNOTATION_BEAN", "InitAnnotationBean"),
    APOLLO("APOLLO", "Apollo"),
    OPEN_FEIGN("OPEN_FEIGN", "OpenFeign"),
    SWAGGER("SWAGGER", "Swagger");

    private final String componentName;

    private final String showName;

    SpringComponentEnum(String componentName, String showName) {
        this.componentName = componentName;
        this.showName = showName;
    }

}
