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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class ApolloCreatorPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor implements ApplicationListener<LoadApolloNamespaceEvent> {

    /**
     * 避免多线程访问
     */
    private static final Lock lock = new ReentrantLock(true);

    private static final AtomicBoolean started = new AtomicBoolean(Boolean.FALSE);

    private static final AtomicBoolean loadingNameSpace = new AtomicBoolean(Boolean.FALSE);

    /**
     * @return
     * @see com.ctrip.framework.apollo.spring.boot.ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     */
    public ApolloCreatorPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    @Override
    public boolean isReady() {
        return !started.get();
    }

    /**
     * 1. 初始化ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment)
     *
     * @param invokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO) {
        lock.lock();
        super.atMethodInvokeBefore(invokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        super.atMethodInvokeAfter(invokeVO, invokeDetail);
        //1. ApolloApplicationContextInitializer#initialize(ConfigurableEnvironment) 初始化完毕
        if (loadingNameSpace.get()) {
            started.compareAndSet(false, true);
            component.remove();
        }
        lock.unlock();
    }

    @Override
    public void onApplicationEvent(LoadApolloNamespaceEvent event) {
        if (component.get() != null) {
            //2. 初始化Apollo命名空间
            loadingNameSpace.compareAndSet(false, true);
            InitializedComponent initializedComponent = component.get();
            initializedComponent.insertChildren(event.getLoadedNamespace());
        }
    }

}
