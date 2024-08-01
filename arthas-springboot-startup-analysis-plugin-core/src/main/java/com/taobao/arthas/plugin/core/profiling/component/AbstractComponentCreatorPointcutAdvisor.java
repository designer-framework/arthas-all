package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public abstract class AbstractComponentCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook {

    protected final ComponentEnum componentEnum;

    private final ThreadLocal<InitializedComponent> component = new ThreadLocal<>();

    @Getter
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
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //首次启动组件
        if (started.compareAndSet(false, true)) {
            component.set(InitializedComponent.root(componentEnum));
        }

        //子条目
        InitializedComponent initializedComponent = getInitializedComponent();
        if (initializedComponent != null) {
            addComponentChild(invokeVO, methodInvokeVO.getStartMillis());
        }
    }

    protected InitializedComponent getInitializedComponent() {
        return component.get();
    }

    /**
     * 组件子条目, 如果没有子条目则不会进该方法
     *
     * @param invokeVO
     * @return
     */
    protected InitializedComponent.Children addComponentChild(InvokeVO invokeVO, BigDecimal startMillis) {
        String childItemName = childItemName(invokeVO);

        if (childItemName != null) {

            getInitializedComponent()
                    .insertChildren(new InitializedComponent.Children(childItemName, startMillis));

        }

        return new InitializedComponent.Children(childItemName, startMillis);
    }

    protected String childItemName(InvokeVO invokeVO) {
        return null;
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        //继承方法调用的耗时
        InitializedComponent initializedComponent = getInitializedComponent();
        initializedComponent.setEndMillis(invokeDetail.getEndMillis());
        initializedComponent.setDuration(invokeDetail.getDuration());

        //子节点耗时
        if (!CollectionUtils.isEmpty(initializedComponent.getChildren())) {

            List<InitializedComponent.Children> children = initializedComponent.getChildren();
            InitializedComponent.Children child = children.get(children.size() - 1);
            child.setEndMillis(invokeDetail.getEndMillis());
            child.setDuration(invokeDetail.getDuration());

        }

    }

    @Override
    public void stop() {
        if (started.get()) {
            InitializedComponent initializedComponent = getInitializedComponent();
            initializedComponent.updateDurationByChildren();
            applicationEventPublisher.publishEvent(new ComponentInitializedEvent(this, initializedComponent));
        }
    }

    @Override
    public void destroy() {
        if (started.get()) {
            super.destroy();
            component.remove();
        }
    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.UPLOAD_STATISTICS;
    }

}
