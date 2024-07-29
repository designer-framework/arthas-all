package com.taobao.arthas.plugin.core.enums;

import lombok.Getter;

@Getter
public enum SpringComponentEnum implements ComponentEnum {

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
