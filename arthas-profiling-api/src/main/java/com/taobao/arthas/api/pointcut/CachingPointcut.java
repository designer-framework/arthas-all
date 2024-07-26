package com.taobao.arthas.api.pointcut;

import com.taobao.arthas.api.source.AgentSourceAttribute;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
public class CachingPointcut implements Pointcut {

    private final boolean canRetransform;

    private final AgentSourceAttribute agentSourceAttribute;

    public CachingPointcut(AgentSourceAttribute agentSourceAttribute, Boolean canRetransform) {
        this.agentSourceAttribute = agentSourceAttribute;
        this.canRetransform = canRetransform;
    }

    public boolean getCanRetransform() {
        return canRetransform;
    }

    @Override
    public boolean isCandidateClass(String className) {
        return agentSourceAttribute.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        return agentSourceAttribute.isCandidateMethod(className, methodName, methodDesc);
    }

    @Override
    public boolean isHit(String className, String methodName, String methodDesc) {
        return agentSourceAttribute.getSourceAttribute(className, methodName, methodDesc) != null;
    }

}
