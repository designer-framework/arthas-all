package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.lifecycle.AgentLifeCycle;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.hook.AgentLifeCycleStopHook;
import com.taobao.arthas.core.vo.AgentStatistics;
import com.taobao.arthas.core.vo.DurationUtils;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractLifeCyclePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor {

    private final AgentLifeCycle agentLifeCycle;

    private final AgentStatistics agentStatistics;

    private BigDecimal startTime = BigDecimal.ZERO;

    public AbstractLifeCyclePointcutAdvisor(AgentLifeCycle agentLifeCycle, AgentStatistics agentStatistics) {
        super();
        this.agentLifeCycle = agentLifeCycle;
        this.agentStatistics = agentStatistics;
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return true;
    }

    /**
     * 项目启动时间
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        //调用start, 启动性能分析
        agentLifeCycle.start();

        //标记开始分析
        getAgentState().start();

        //性能分析起始时间
        startTime = DurationUtils.nowMillis();
    }

    /**
     * 项目启动完成, 发布分析完成事件
     *
     * @param invokeVO
     * @see AgentLifeCycleStopHook
     */
    @Override
    public void atExit(InvokeVO invokeVO) {
        //性能分析耗时
        agentStatistics.setAgentTime(DurationUtils.nowMillis().subtract(startTime));

        //标记分析完毕
        getAgentState().stop();

        //分析完毕, 通知释放资源,关闭容器,上报分析数据...
        agentLifeCycle.stop();
    }

}
