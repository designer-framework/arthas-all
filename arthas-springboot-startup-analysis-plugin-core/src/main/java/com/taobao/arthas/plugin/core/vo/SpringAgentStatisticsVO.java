package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.core.vo.AgentStatisticsVO;
import lombok.Getter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.function.Consumer;

public class SpringAgentStatisticsVO extends AgentStatisticsVO implements SpringAgentStatistics {

    private final Map<String, BeanCreateVO> createdBeansMap = new ConcurrentHashMap<>();

    @Getter
    private final Collection<InitializedComponent> initializedComponents = new ConcurrentLinkedDeque<>();

    @Override
    public void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer) {
        consumer.accept(createdBeansMap.get(beanName));
    }

    @Override
    public void addCreatedBean(BeanCreateVO beanCreateVO) {
        this.createdBeansMap.put(beanCreateVO.getName(), beanCreateVO);
    }

    @Override
    public void addInitializedComponents(Collection<InitializedComponent> initializedComponents) {
        this.initializedComponents.addAll(initializedComponents);
    }


    @Override
    public Collection<BeanCreateVO> getCreatedBeans() {
        return createdBeansMap.values();
    }

}
