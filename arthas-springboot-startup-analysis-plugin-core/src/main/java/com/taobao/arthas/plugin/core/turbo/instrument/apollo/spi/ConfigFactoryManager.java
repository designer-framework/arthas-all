package com.taobao.arthas.plugin.core.turbo.instrument.apollo.spi;

public interface ConfigFactoryManager {
    ConfigFactory getFactory(String namespace);
}
