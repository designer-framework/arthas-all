package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor {

    @Override
    public boolean isCandidateClass(String className) {
        return false;
    }

    @Override
    public boolean isCandidateMethod0(String className, String methodName, String methodDesc) {
        return false;
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
    }

}
