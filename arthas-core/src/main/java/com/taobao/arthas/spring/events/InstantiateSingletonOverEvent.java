package com.taobao.arthas.spring.events;

import lombok.Data;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Data
public class InstantiateSingletonOverEvent extends BeanCreationEvent {

    private final long startTime;

    private final String beanName;

    private long endTime;

    private long costTime;

    public InstantiateSingletonOverEvent(Object source, String beanName) {
        super(source);
        this.beanName = beanName;
        startTime = System.currentTimeMillis();
    }

    public InstantiateSingletonOverEvent instantiated() {
        endTime = System.currentTimeMillis();
        costTime = endTime - startTime;
        return this;
    }

}
