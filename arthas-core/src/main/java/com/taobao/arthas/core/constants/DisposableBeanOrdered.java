package com.taobao.arthas.core.constants;

import org.springframework.core.Ordered;

public class DisposableBeanOrdered {

    /**
     * 1. 启动性能分析报告展示服务器
     */
    public static final Integer START_REPORTER_SERVER = Ordered.HIGHEST_PRECEDENCE;


    public static final Integer RELEASE_METHOD_INVOKE = START_REPORTER_SERVER - 10;

    /**
     * 释放Agent钩子资源
     */
    public static final Integer RELEASE_ARTHAS_AGENT = Ordered.LOWEST_PRECEDENCE;

}
