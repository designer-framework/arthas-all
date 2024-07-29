package com.taobao.arthas.plugin.core.advisor;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor {

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(componentEnum, classMethodInfo, interceptor);
    }

}
