package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.LoadApolloNamespaceEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor implements ApplicationListener<LoadApolloNamespaceEvent> {

    private static final AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        super.atExit(invokeVO);
        component.remove();
    }

    @Override
    public void onApplicationEvent(LoadApolloNamespaceEvent event) {
        if (component.get() != null && started.compareAndSet(false, true)) {
            InitializedComponent initializedComponent = component.get();
            initializedComponent.insertChildren(event.getLoadedNamespace());
        }
    }

}
