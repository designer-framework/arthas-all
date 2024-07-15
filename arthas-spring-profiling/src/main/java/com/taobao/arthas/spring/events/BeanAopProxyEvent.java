package com.taobao.arthas.spring.events;

public class BeanAopProxyEvent extends BeanCreationEvent {

    public BeanAopProxyEvent(Object source) {
        super(source);
    }

}
