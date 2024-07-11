package com.taobao.arthas.profiling.api.processor;

public interface LifeCycle {

    /**
     * 容器启动
     */
    void start();

    /**
     * 容器销毁
     */
    void stop();

}
