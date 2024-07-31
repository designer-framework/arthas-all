package com.taobao.arthas.core.constants;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

public class LifeCycleOrdered {

    //------START------//
    /**
     * 0. 开始性能分析
     */
    //public static final Integer STARTING_PROFILING = Ordered.LOWEST_PRECEDENCE;


    //------STOP------//
    /**
     * 1. 性能分析完毕, 停止采集火焰图数据
     */
    public static final Integer STOP_FLAME_GRAPH_PROFILER = Ordered.HIGHEST_PRECEDENCE;

    /**
     * 2. 上报分析完成的数据
     */
    public static final Integer UPLOAD_STATISTICS = STOP_FLAME_GRAPH_PROFILER + 20;

    /**
     * 3. 性能分析完毕, 停止容器, 触发Bean销毁, 释放资源
     *
     * @see DisposableBean#destroy()
     */
    public static final Integer STOP_PROFILER_CONTAINER = UPLOAD_STATISTICS + 20;

}
