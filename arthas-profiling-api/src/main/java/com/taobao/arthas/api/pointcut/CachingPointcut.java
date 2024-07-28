package com.taobao.arthas.api.pointcut;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.source.AgentSourceAttribute;
import lombok.Data;
import org.springframework.util.Assert;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
@Data
public class CachingPointcut implements Pointcut {

    private final boolean canRetransform;

    private final AgentSourceAttribute agentSourceAttribute;

    private Class<? extends SpyInterceptorApi> interceptor;

    public CachingPointcut(AgentSourceAttribute agentSourceAttribute, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        this.agentSourceAttribute = agentSourceAttribute;
        this.canRetransform = canRetransform;
        this.interceptor = interceptor;
        Assert.notNull(interceptor, "SpyInterceptorClass");
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
