package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.pointcut.CachingPointcut;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.source.AgentSourceAttribute;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.api.spy.SpyExtensionApiImpl;
import com.taobao.arthas.api.vo.ClassMethodInfo;

public class AdvisorUtils {

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName) {
        return build(abstractMethodInvokePointcutAdvisor, fullyQualifiedMethodName, null);
    }

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(
            T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName
            , Class<? extends SpyExtensionApi> spyExtensionApiClass
    ) {
        //AgentSourceAttribute
        AgentSourceAttribute agentSourceAttribute = new AgentSourceAttribute(ClassMethodInfo.create(fullyQualifiedMethodName));

        //切点
        abstractMethodInvokePointcutAdvisor.setPointcut(getPointcut(agentSourceAttribute, spyExtensionApiClass));
        //
        abstractMethodInvokePointcutAdvisor.setAgentSourceAttribute(agentSourceAttribute);

        return abstractMethodInvokePointcutAdvisor;
    }

    private static Pointcut getPointcut(AgentSourceAttribute agentSourceAttribute, Class<? extends SpyExtensionApi> spyExtensionApiClass) {
        CachingPointcut cachingPointcut = new CachingPointcut(agentSourceAttribute, Boolean.FALSE);
        cachingPointcut.setSpyInterceptorClass(spyExtensionApiClass == null ? SpyExtensionApiImpl.class : spyExtensionApiClass);
        return cachingPointcut;
    }

}
