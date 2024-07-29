package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.core.vo.AgentStatistics;

import java.util.function.Consumer;

public interface SpringAgentStatistics extends AgentStatistics {

    void fillBeanCreate(String beanName, Consumer<BeanCreateVO> consumer);

    void addCreatedBean(BeanCreateVO beanCreateVO);

    void addInitializedComponent(InitializedComponent initializedComponent);

}
