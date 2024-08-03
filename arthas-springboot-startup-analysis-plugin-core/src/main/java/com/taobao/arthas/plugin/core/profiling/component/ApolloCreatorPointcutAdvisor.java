package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Apollo加载配置耗时统计
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentRootCreatorPointcutAdvisor {

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

}
