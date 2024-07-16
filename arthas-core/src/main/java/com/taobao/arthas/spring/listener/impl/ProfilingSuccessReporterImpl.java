package com.taobao.arthas.spring.listener.impl;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.events.ApplicationProfilingOverEvent;
import com.taobao.arthas.spring.listener.Reporter;
import com.taobao.arthas.spring.vo.ReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 21:05
 */
@Component
public class ProfilingSuccessReporterImpl implements ApplicationListener<ApplicationProfilingOverEvent>, Reporter, Ordered {

    @Autowired
    private List<ProfilingLifeCycle> profilingLifeCycles;

    private ApplicationProfilingOverEvent applicationProfilingOverEvent;

    /**
     * 分析完毕, 释放资源
     * 1. 移除Agent代理
     * 2. 启动性能分析报告服务器
     * 3. 关闭容器
     *
     * @param applicationProfilingOverEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(ApplicationProfilingOverEvent applicationProfilingOverEvent) {
        this.applicationProfilingOverEvent = applicationProfilingOverEvent;
        //分析完毕, 释放资源
        profilingLifeCycles.forEach(ProfilingLifeCycle::stop);
    }

    @Override
    public ReportVO getReportVO() {
        return new ReportVO("StartUpTime", applicationProfilingOverEvent.startUpTime());
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
