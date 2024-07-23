package com.taobao.arthas.api.processor;

public interface ProfilingLifeCycle {

    /**
     * 开始分析
     */
    default void start() {
    }

    /**
     * 分析完毕
     */
    void stop();

}
