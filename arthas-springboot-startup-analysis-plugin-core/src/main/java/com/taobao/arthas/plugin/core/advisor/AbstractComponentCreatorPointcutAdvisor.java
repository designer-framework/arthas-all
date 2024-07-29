package com.taobao.arthas.plugin.core.advisor;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class AbstractComponentCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    protected final ComponentEnum componentEnum;

    private final ThreadLocal<InitializedComponent> initializedComponent = new ThreadLocal<>();

    public AbstractComponentCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(classMethodInfo, interceptor);
        this.componentEnum = componentEnum;
    }

    @Override
    protected final void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
        initializedComponent.set(getCreatingComponent(invokeVO));
    }

    protected InitializedComponent getCreatingComponent(InvokeVO invokeVO) {
        return new InitializedComponent(componentEnum);
    }

    @Override
    protected final void atExit(InvokeVO invokeVO) {
        createComponentAfter(invokeVO);
        super.atExit(invokeVO);
    }

    protected void createComponentAfter(InvokeVO invokeVO) {
        applicationEventPublisher.publishEvent(new ComponentInitializedEvent(this, initializedComponent.get().initialized()));
    }

    @Override
    public void destroy() {
        super.destroy();
        initializedComponent.remove();
    }

}
