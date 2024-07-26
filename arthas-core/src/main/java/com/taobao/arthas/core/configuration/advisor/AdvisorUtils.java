package com.taobao.arthas.core.configuration.advisor;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.pointcut.CachingPointcut;
import com.taobao.arthas.api.source.AgentSourceAttribute;
import com.taobao.arthas.api.vo.ClassMethodInfo;

public class AdvisorUtils {

    public static <T extends AbstractMethodInvokePointcutAdvisor> T build(T abstractMethodInvokePointcutAdvisor, String fullyQualifiedMethodName) {
        //AgentSourceAttribute
        AgentSourceAttribute agentSourceAttribute = new AgentSourceAttribute(ClassMethodInfo.create(fullyQualifiedMethodName));

        //切点
        abstractMethodInvokePointcutAdvisor.setPointcut(new CachingPointcut(agentSourceAttribute, Boolean.FALSE));
        abstractMethodInvokePointcutAdvisor.setAgentSourceAttribute(agentSourceAttribute);

        return abstractMethodInvokePointcutAdvisor;
    }

}
