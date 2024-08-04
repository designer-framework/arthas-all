package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.core.vo.AgentStatistics;

import java.util.Collection;
import java.util.function.Consumer;

public interface SpringAgentStatistics extends AgentStatistics {

    void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer);

    Collection<BeanCreateVO> getCreatedBeans();

    void addCreatedBean(BeanCreateVO beanCreateVO);

    void addInitializedComponents(Collection<InitializedComponent> initializedComponents);

    Collection<InitializedComponent> getInitializedComponents();

}
