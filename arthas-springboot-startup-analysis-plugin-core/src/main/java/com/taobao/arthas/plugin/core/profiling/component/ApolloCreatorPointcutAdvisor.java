package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.LoadApolloNamespaceEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Apollo加载配置耗时统计
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor implements ApplicationListener<LoadApolloNamespaceEvent> {

    private final AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

    private final AtomicBoolean loadingNameSpace = new AtomicBoolean(Boolean.FALSE);

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return !started.get();
    }

    @Override
    public void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    /**
     * 1. ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment) 初始化完毕
     *
     * @param invokeVO
     * @param invokeDetail
     */
    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        super.atMethodInvokeAfter(invokeVO, invokeDetail);
        if (loadingNameSpace.get()) {
            started.compareAndSet(false, true);
        }
    }

    @Override
    public void onApplicationEvent(LoadApolloNamespaceEvent event) {
        if (getInitializedComponent() != null) {
            loadingNameSpace.compareAndSet(false, true);
            InitializedComponent initializedComponent = getInitializedComponent();
            initializedComponent.insertChildren(event.getLoadedNamespace());
        }
    }

}
