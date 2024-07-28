package com.taobao.arthas.api.pointcut;

import com.taobao.arthas.api.source.AgentSourceAttribute;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.api.spy.SpyExtensionApiImpl;
import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
@Data
public class CachingPointcut implements Pointcut {

    private final boolean canRetransform;

    private final AgentSourceAttribute agentSourceAttribute;

    private Class<? extends SpyExtensionApi> spyInterceptorClass;

    public CachingPointcut(AgentSourceAttribute agentSourceAttribute, Boolean canRetransform) {
        this.agentSourceAttribute = agentSourceAttribute;
        this.canRetransform = canRetransform;
        this.spyInterceptorClass = SpyExtensionApiImpl.class;
    }

    @Override
    public boolean getCanRetransform() {
        return canRetransform;
    }

    @Override
    public boolean isCandidateClass(String className) {
        return agentSourceAttribute.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        return this.agentSourceAttribute.isCandidateMethod(className, methodName, methodDesc);
    }

    @Override
    public boolean isHit(String className, String methodName, String methodDesc) {
        return this.agentSourceAttribute.getSourceAttribute(className, methodName, methodDesc) != null;
    }

}
