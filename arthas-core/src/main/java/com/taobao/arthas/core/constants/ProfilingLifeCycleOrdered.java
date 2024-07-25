package com.taobao.arthas.core.constants;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

public class ProfilingLifeCycleOrdered {

    //------START------//
    /**
     * 0. 开始性能分析
     */
    //public static final Integer STARTING_PROFILING = Ordered.LOWEST_PRECEDENCE;


    //------STOP------//
    /**
     * 1. 性能分析完毕, 停止采集火焰图数据
     */
    public static final Integer STOP_STACK_TRACE_PROFILER = Ordered.LOWEST_PRECEDENCE;

    /**
     * 2. 性能分析完毕, 停止容器,触发Bean销毁
     *
     * @see DisposableBean#destroy()
     */
    public static final Integer STOP_CONTAINER = STOP_STACK_TRACE_PROFILER - 1;

}
