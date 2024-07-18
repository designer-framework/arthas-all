package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring项目启动耗时分析
 */
@Component
public class SpringContainerLifeCycleAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, Ordered {

    @Autowired
    private List<ProfilingLifeCycle> profilingLifeCycles;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    private long startTime = 0L;

    public SpringContainerLifeCycleAdviceHandler() {
        super(FullyQualifiedClassUtils.parserClassMethodInfo(
                "org.springframework.boot.SpringApplication" +
                        "#run(java.lang.Class, java.lang.String[])"
        ));
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
        profilingResultVO.setStartUpTime(System.currentTimeMillis() - startTime);
        //分析完毕, 释放资源
        profilingLifeCycles.forEach(ProfilingLifeCycle::stop);

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

}
