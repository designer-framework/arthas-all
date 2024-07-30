package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SwaggerCreatorPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor {

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#SPRINGFOX_DOCUMENTATION_AUTO_STARTUP
     */
    public SwaggerCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        super.atExit(invokeVO);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
    }

}
