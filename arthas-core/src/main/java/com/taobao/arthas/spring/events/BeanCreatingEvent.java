package com.taobao.arthas.spring.events;

import com.taobao.arthas.spring.vo.BeanCreateVO;

public class BeanCreatingEvent extends BeanCreationEvent {

    private final BeanCreateVO beanCreateVO;

    public BeanCreatingEvent(Object source, BeanCreateVO beanCreateVO) {
        super(source);
        this.beanCreateVO = beanCreateVO;
    }

    public BeanCreateVO getBeanCreateVO() {
        return beanCreateVO;
    }

}
