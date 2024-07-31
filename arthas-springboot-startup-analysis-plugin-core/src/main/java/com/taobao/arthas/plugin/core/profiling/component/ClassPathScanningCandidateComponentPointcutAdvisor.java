package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ClassPathScanningCandidateComponentPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook {

    protected final ThreadLocal<InitializedComponent> componentChildren;

    /**
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    public ClassPathScanningCandidateComponentPointcutAdvisor(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo);
        this.componentChildren = ThreadLocal.withInitial(() -> InitializedComponent.root(SpringComponentEnum.CLASS_PATH_SCANNING));
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        componentChildren.get().insertChildren(
                new InitializedComponent.Children(
                        String.valueOf(getParams(invokeVO)[0]), invokeDetail.getDuration()
                )
        );
    }

    @Override
    public void stop() {

        InitializedComponent initializedComponent = componentChildren.get();
        if (!initializedComponent.getChildren().isEmpty()) {
            applicationEventPublisher.publishEvent(
                    new ComponentInitializedEvent(this, initializedComponent.updateDurationByChildren())
            );
        }

    }

}
