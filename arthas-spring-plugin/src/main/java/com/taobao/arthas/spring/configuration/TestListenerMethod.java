package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.handler.MethodInvokeListener;
import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TestListenerMethod extends AbstractMethodInvokePointcutAdvisor implements MethodInvokeListener, ClassMethodMatchPointcut {

    @Override
    public boolean isCandidateClass(String className) {
        return false;
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return false;
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {

    }

    @Override
    public void before(InvokeVO invokeVO) {
        log.error("");
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) {
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) {
    }

}
