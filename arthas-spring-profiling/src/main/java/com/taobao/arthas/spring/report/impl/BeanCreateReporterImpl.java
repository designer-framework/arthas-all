package com.taobao.arthas.spring.report.impl;

import com.taobao.arthas.spring.events.BeanCreatedEvent;
import com.taobao.arthas.spring.report.BeanCreateReporter;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BeanCreateReporterImpl implements BeanCreateReporter, ApplicationListener<BeanCreatedEvent> {

    private List<BeanCreatedEvent> beanCreatedEvents;

    @Override
    public void onApplicationEvent(BeanCreatedEvent event) {
        beanCreatedEvents.add(event);
    }

    public List<BeanCreatedEvent> getBeanCreatedEvents() {
        return beanCreatedEvents;
    }

}
