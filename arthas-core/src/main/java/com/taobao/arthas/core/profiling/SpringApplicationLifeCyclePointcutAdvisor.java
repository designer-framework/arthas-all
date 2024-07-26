package com.taobao.arthas.core.profiling;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.lifecycle.LifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.profiling.hook.AnalysisStopLifeCycleHook;
import com.taobao.arthas.core.profiling.hook.FlameGraphLifeCycleHook;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
@Component
public class SpringApplicationLifeCyclePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements InitializingBean {

    @Autowired
    protected ApplicationEventPublisher eventPublisher;

    @Autowired
    private List<LifeCycle> lifeCycles;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    private long startTime = 0L;

    public SpringApplicationLifeCyclePointcutAdvisor() {
        super("org.springframework.boot.SpringApplication#run(java.lang.Class, java.lang.String[])");
    }

    @Override
    public boolean isReady() {
        return true;
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
        lifeCycles.forEach(LifeCycle::start);

        agentState.start();
    }

    /**
     * 项目启动完成, 发布分析完成事件
     *
     * @param invokeVO
     * @see FlameGraphLifeCycleHook
     * @see AnalysisStopLifeCycleHook
     */
    @Override
    public void atExit(InvokeVO invokeVO) {
        //启动耗时
        profilingResultVO.setStartUpTime(System.currentTimeMillis() - startTime);

        agentState.stop();
        //分析完毕, 通知释放资源,关闭容器,上报分析数据...
        lifeCycles.forEach(LifeCycle::stop);

    }

}
