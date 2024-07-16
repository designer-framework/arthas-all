package com.taobao.arthas.spring.events;

public class BeanAopProxyCreatedEvent extends BeanCreationEvent {

    private long startTime;

    private long endTime;

    private long costTime;

    private String beanName;

    public BeanAopProxyCreatedEvent(Object source, String beanName, long startTime) {
        super(source);
        this.beanName = beanName;
        this.startTime = startTime;
        this.endTime = System.currentTimeMillis();
        this.costTime = endTime - startTime;
    }

    public long getCostTime() {
        return costTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public String getBeanName() {
        return beanName;
    }
    
}
