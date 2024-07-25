package com.taobao.arthas.api.state;

public interface AgentState {

    /**
     * 开始性能分析
     */
    void started();

    /**
     * 性能分析完毕
     */
    void stop();

    /**
     * 是否已经开始性能分析
     */
    boolean isStarted();

}
