package com.taobao.arthas.spring.events;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
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

    public String getBeanName() {
        return beanName;
    }

    public long getCostTime() {
        return costTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public InstantiateSingletonOverEvent instantiated() {
        endTime = System.currentTimeMillis();
        costTime = endTime - startTime;
        return this;
    }

}
