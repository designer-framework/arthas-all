package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.properties.ArthasProperties;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class SpringBeanCreateAdviceHandler implements InvokeAdviceHandler {

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final String[] methodArgTypes = new String[]{"java.lang.String", "org.springframework.beans.factory.support.RootBeanDefinition", "java.lang.Object[]"};

    private final Set<String> traceMethods;

    public SpringBeanCreateAdviceHandler(ArthasProperties arthasProperties) {
        this.traceMethods = arthasProperties.traceMethods();
    }

    @Override
    public boolean isCandidateClass(String className) {
        return "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory".equals(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        //
        if (!"doCreateBean".equals(methodName)) {
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
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) {
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) {
    }

}
