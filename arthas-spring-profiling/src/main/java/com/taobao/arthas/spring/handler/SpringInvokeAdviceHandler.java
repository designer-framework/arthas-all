package com.taobao.arthas.spring.handler;

import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;

public class SpringInvokeAdviceHandler implements InvokeAdviceHandler {

    @Override
    public boolean isCandidateClass(String className) {
        return "Main".equals(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return "main".equals(methodName);
    }

    @Override
    public void handler(InvokeVO invokeVO) {
        System.out.println(2);
    }

}
