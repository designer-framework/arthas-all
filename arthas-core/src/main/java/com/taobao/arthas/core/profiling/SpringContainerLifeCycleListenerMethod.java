package com.taobao.arthas.core.profiling;

import com.taobao.arthas.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.profiling.hook.ArthasExtensionShutdownHookPostProcessor;
import com.taobao.arthas.core.profiling.stacktrace.SpringStacktraceProfiler;
import com.taobao.arthas.core.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
@Component
public class SpringContainerLifeCycleListenerMethod extends AbstractMethodMatchInvokePointcutAdvisor {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    private List<ProfilingLifeCycle> profilingLifeCycles;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    private long startTime = 0L;

    public SpringContainerLifeCycleListenerMethod() {
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

        //调用start, 启动性能分析Bean
        profilingLifeCycles.forEach(ProfilingLifeCycle::start);
    }

    /**
     * 项目启动完成, 发布分析完成事件
     *
     * @param invokeVO
     * @see SpringStacktraceProfiler
     * @see ArthasExtensionShutdownHookPostProcessor
     */
    @Override
    public void atExit(InvokeVO invokeVO) {
        //启动耗时
        profilingResultVO.setStartUpTime(System.currentTimeMillis() - startTime);

        //分析完毕, 通知释放资源,关闭容器,上报分析数据...

        profilingLifeCycles.forEach(ProfilingLifeCycle::stop);

    }

}
