package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractComponentCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    protected final ComponentEnum componentEnum;

    protected final ThreadLocal<InitializedComponent> component = new ThreadLocal<>();

    private final AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

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
    protected void atMethodInvokeBefore(InvokeVO invokeVO) {
        //首次启动组件
        if (started.compareAndSet(false, true)) {
            component.set(InitializedComponent.root(componentEnum));
        }
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        InitializedComponent initializedComponent = component.get();
        initializedComponent.initialized();
        applicationEventPublisher.publishEvent(new ComponentInitializedEvent(this, initializedComponent));
    }

    @Override
    public void destroy() {
        super.destroy();
        component.remove();
    }

}
