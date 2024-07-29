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
public abstract class AbstractComponentCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    protected final ComponentEnum componentEnum;

    protected final ThreadLocal<InitializedComponent> component = new ThreadLocal<>();

    public AbstractComponentCreatorPointcutAdvisor(
            ComponentEnum componentEnum,
            ClassMethodInfo classMethodInfo
    ) {
        super(classMethodInfo);
        this.componentEnum = componentEnum;
    }

    public AbstractComponentCreatorPointcutAdvisor(
            ComponentEnum componentEnum, ClassMethodInfo classMethodInfo
            , Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(classMethodInfo, interceptor);
        this.componentEnum = componentEnum;
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
        component.set(getCreatingComponent(invokeVO));
    }

    /**
     * 初始化图标依赖的数据
     *
     * @param invokeVO
     * @return
     */
    protected InitializedComponent getCreatingComponent(InvokeVO invokeVO) {
        return InitializedComponent.root(componentEnum);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        createComponentAfter(invokeVO);
        super.atExit(invokeVO);
    }

    protected void createComponentAfter(InvokeVO invokeVO) {
        applicationEventPublisher.publishEvent(new ComponentInitializedEvent(this, component.get().initialized()));
    }

    @Override
    public void destroy() {
        super.destroy();
        component.remove();
    }

}
