package com.taobao.arthas.core.constants;

import org.springframework.core.Ordered;

public class LifeCycleStopHookOrdered {

    // -------------      启动性能报告服务器      ------------- //
    /**
     * 1. 异步启动分析报告Http服务器
     */
    public static final Integer START_REPORTER_SERVER = Ordered.HIGHEST_PRECEDENCE;


    // -------------释放性能分析过程中占用的内存资源------------- //
    /**
     *
     */
    public static final Integer RELEASE_METHOD_INVOKE = START_REPORTER_SERVER - 10;

    /**
     * 释放Agent钩子资源
     */
    public static final Integer RELEASE_ARTHAS_AGENT = Ordered.LOWEST_PRECEDENCE;

}
