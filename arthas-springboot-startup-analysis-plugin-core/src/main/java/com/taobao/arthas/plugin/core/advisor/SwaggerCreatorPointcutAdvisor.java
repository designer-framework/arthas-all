package com.taobao.arthas.plugin.core.advisor;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class SwaggerCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    /**
     * @return
     * @see springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper#SPRINGFOX_DOCUMENTATION_AUTO_STARTUP
     */
    public SwaggerCreatorPointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(classMethodInfo, interceptor);
    }

}
