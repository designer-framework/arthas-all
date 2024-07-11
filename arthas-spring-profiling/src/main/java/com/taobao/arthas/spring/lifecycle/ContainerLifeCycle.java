package com.taobao.arthas.spring.lifecycle;

import com.taobao.arthas.profiling.api.processor.LifeCycle;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ContainerLifeCycle implements LifeCycle {

    private static long startTime = 0L;

    private final AnnotationConfigApplicationContext annotationConfigApplicationContext;

    public ContainerLifeCycle(AnnotationConfigApplicationContext annotationConfigApplicationContext) {
        this.annotationConfigApplicationContext = annotationConfigApplicationContext;
    }

    @Override
    public void start() {
        //
        startTime = System.currentTimeMillis();
    }

    @Override
    public void stop() {
        //启动耗时
        double startupDuration = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println("启动耗时: " + startupDuration);
    }

}
