package com.taobao.arthas.plugin.core.events;

import lombok.Getter;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class BeanInitMethodInvokeEvent extends BeanCreationEvent {

    public BeanInitMethodInvokeEvent(Object source, String beanName) {
        super(source, beanName);
    }

}
