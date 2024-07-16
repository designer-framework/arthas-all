package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.ApplicationProfilingOverEvent;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * Spring项目启动耗时分析
 */
@Component
public class SpringContainerLifeCycleAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, Ordered {

    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.boot.SpringApplication" +
                    "#run(java.lang.Class, java.lang.String[])"
    );

    private long startTime = 0L;

    @Override
    public boolean isCandidateClass(String className) {
        return traceMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return traceMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

    /**
     * 项目启动时间
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        startTime = System.currentTimeMillis();
    }

    /**
     * 项目启动完成, 发布分析完成事件
     *
     * @param invokeVO
     */
    @Override
    public void atExit(InvokeVO invokeVO) {
        //启动耗时

        //项目启动完成
        eventPublisher.publishEvent(new ApplicationProfilingOverEvent(this, startTime));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
