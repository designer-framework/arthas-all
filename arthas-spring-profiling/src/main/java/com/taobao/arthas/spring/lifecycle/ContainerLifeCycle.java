package com.taobao.arthas.spring.lifecycle;

import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import org.springframework.stereotype.Component;

@Component
public class ContainerLifeCycle implements InvokeAdviceHandler {

    private static long startTime = 0L;

    private final String[] methodArgTypes = new String[]{"java.lang.Class", "java.lang.String[]"};

    @Override
    public boolean isCandidateClass(String className) {
        return "org.springframework.boot.SpringApplication".equals(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {

        if (!"run".equals(methodName)) {
            return false;
        }

        for (int i = 0; i < this.methodArgTypes.length; i++) {
            if (!this.methodArgTypes[i].equals(methodArgTypes[i])) {
                return false;
            }
        }
        return true;

    }

    @Override
    public void before(InvokeVO invokeVO) {
        //
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) {
        //启动耗时
        double startupDuration = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println("启动耗时: " + startupDuration);

    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) {
        //启动耗时
        double startupDuration = (System.currentTimeMillis() - startTime) / 1000D;
        System.out.println("启动耗时: " + startupDuration);

    }
}
