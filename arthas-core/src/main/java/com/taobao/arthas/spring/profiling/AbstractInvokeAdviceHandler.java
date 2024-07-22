package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
import lombok.Getter;

@Getter
public abstract class AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    private ClassMethodInfo classMethodInfo;

    public AbstractInvokeAdviceHandler(ClassMethodInfo classMethodInfo) {
        this.classMethodInfo = classMethodInfo;
    }

    @Override
    public boolean isCandidateClass(String className) {
        return classMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return classMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

    public boolean isReady() {
        return true;
    }

    @Override
    public final void before(InvokeVO invokeVO) {
        String className = invokeVO.getClazz().getName();
        if (isReady() && isCandidateClass(className) && isCandidateMethod(className, invokeVO.getMethodName(), invokeVO.getMethodArgumentTypes())) {
            atBefore(invokeVO);
        }
    }

    protected abstract void atBefore(InvokeVO invokeVO);

    @Override
    public final void afterReturning(InvokeVO invokeVO) {
        String className = invokeVO.getClazz().getName();
        if (isReady() && isCandidateClass(className) && isCandidateMethod(className, invokeVO.getMethodName(), invokeVO.getMethodArgumentTypes())) {
            atAfterReturning(invokeVO);
        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    @Override
    public final void afterThrowing(InvokeVO invokeVO) {
        String className = invokeVO.getClazz().getName();
        if (isReady() && isCandidateClass(className) && isCandidateMethod(className, invokeVO.getMethodName(), invokeVO.getMethodArgumentTypes())) {
            atAfterThrowing(invokeVO);
        }
    }

    protected void atAfterThrowing(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected abstract void atExit(InvokeVO invokeVO);

}
