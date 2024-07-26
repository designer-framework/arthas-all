package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.lifecycle.LifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.hook.StopLifeCycleHook;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SpringApplicationLifeCyclePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements InitializingBean {

    private final List<LifeCycle> lifeCycles;

    private final ProfilingResultVO profilingResultVO;

    private long startTime = 0L;

    public SpringApplicationLifeCyclePointcutAdvisor(List<LifeCycle> lifeCycles, ProfilingResultVO profilingResultVO) {
        super();
        this.lifeCycles = lifeCycles;
        this.profilingResultVO = profilingResultVO;
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
     * @see StopLifeCycleHook
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
