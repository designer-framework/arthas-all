package com.taobao.arthas.api.lifecycle;

public interface LifeCycle {

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
