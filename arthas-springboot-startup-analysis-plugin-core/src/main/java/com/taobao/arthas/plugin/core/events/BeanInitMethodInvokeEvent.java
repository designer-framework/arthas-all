package com.taobao.arthas.plugin.core.events;

import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class BeanInitMethodInvokeEvent extends BeanCreationEvent {

    private final String initMethods;

    public BeanInitMethodInvokeEvent(Object source, String beanName, String initMethods) {
        super(source, beanName);
        this.initMethods = initMethods;
    }

}
