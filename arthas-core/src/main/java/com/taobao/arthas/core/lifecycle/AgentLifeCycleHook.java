package com.taobao.arthas.core.lifecycle;

/**
 * 性能分析钩子
 */
public interface AgentLifeCycleHook {

    /**
     * 开始分析
     */
    default void start() {
    }

    /**
     * 分析完毕
     */
    default void stop() {
    }

}
