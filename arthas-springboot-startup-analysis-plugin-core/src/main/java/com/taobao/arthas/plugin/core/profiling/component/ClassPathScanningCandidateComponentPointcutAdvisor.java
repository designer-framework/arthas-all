package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentRootInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ClassPathScanningCandidateComponentPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements AgentLifeCycleHook {

    /**
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    public ClassPathScanningCandidateComponentPointcutAdvisor(SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, SpringAgentStatistics springAgentStatistics) {
        super(springComponentEnum, classMethodInfo);
    }

    /**
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#ClassPathScanningCandidateComponentProvider(boolean)
     */
    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.CLASS_PATH_SCANNING_CANDIDATE, BigDecimal.ZERO, true)
        ));

    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[0]);
    }

}
