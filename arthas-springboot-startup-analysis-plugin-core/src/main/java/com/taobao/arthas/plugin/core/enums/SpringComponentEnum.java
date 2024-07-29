package com.taobao.arthas.plugin.core.enums;

public enum SpringComponentEnum implements ComponentEnum {

    APOLLO,
    OPEN_FEIGN,
    SWAGGER;

    SpringComponentEnum() {
    }

    @Override
    public String getName() {
        return toString();
    }

}
