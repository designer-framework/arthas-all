package com.taobao.arthas.spring.report;

import com.taobao.arthas.spring.events.BeanCreatedEvent;

import java.util.List;

public interface BeanCreateReporter {

    List<BeanCreatedEvent> getBeanCreatedEvents();

}
