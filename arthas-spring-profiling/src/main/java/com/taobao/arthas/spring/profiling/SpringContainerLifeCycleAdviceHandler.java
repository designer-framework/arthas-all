package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SpringContainerLifeCycleAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, Ordered {

    private static long startTime = 0L;

    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.boot.SpringApplication#run(" +
                    "java.lang.Class, java.lang.String[]" +
                    ")"
    );

    @Autowired
    private List<ProfilingLifeCycle> profilingLifeCycles;

    @Override
    public boolean isCandidateClass(String className) {
        return traceMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return traceMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

    @Override
    public void atBefore(InvokeVO invokeVO) {

        //
        startTime = System.currentTimeMillis();

    }

    @Override
    public void atExit(InvokeVO invokeVO) {

        //启动耗时
        double startupDuration = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println("项目启动耗时: " + startupDuration);

        profilingLifeCycles.forEach(ProfilingLifeCycle::stop);

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
