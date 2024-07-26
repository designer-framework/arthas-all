package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.source.AgentSourceAttribute;

/**
 * @description: 切面
 * @author: Designer
 * @date : 2024-07-23 22:40
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 切点
     *
     * @return
     */
    Pointcut getPointcut();

    AgentSourceAttribute getAgentSourceAttribute();

}
