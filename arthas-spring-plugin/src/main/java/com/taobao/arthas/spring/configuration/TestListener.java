package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import org.springframework.stereotype.Component;

@Component
public class TestListener implements InvokeAdviceHandler, MatchCandidate {

    @Override
    public boolean isCandidateClass(String className) {
        return false;
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return false;
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
