package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.pointcut.CachingPointcut;
import com.taobao.arthas.api.source.AgentSourceAttribute;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;

public class AdvisorUtils {

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName) {
        return build(abstractMethodInvokePointcutAdvisor, fullyQualifiedMethodName, null);
    }

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(
            T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName
            , Class<? extends SpyInterceptorApi> spyExtensionApiClass
    ) {
        //AgentSourceAttribute
        AgentSourceAttribute agentSourceAttribute = new AgentSourceAttribute(ClassMethodInfo.create(fullyQualifiedMethodName));

        //切点
        abstractMethodInvokePointcutAdvisor.setPointcut(
                new CachingPointcut(agentSourceAttribute, Boolean.FALSE, spyExtensionApiClass == null ? SimpleSpyInterceptorApi.class : spyExtensionApiClass)
        );
        
        //
        abstractMethodInvokePointcutAdvisor.setAgentSourceAttribute(agentSourceAttribute);

        return abstractMethodInvokePointcutAdvisor;
    }

}
