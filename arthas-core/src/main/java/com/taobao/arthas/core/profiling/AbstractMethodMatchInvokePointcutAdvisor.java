package com.taobao.arthas.core.profiling;

import com.taobao.arthas.core.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import lombok.Getter;

@Getter
public abstract class AbstractMethodMatchInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor {

    protected final ClassMethodInfo classMethodInfo;

    public AbstractMethodMatchInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        this.classMethodInfo = classMethodInfo;
    }

    @Override
    public final boolean isCandidateClass(String className) {
        return classMethodInfo.isCandidateClass(className);
    }

    @Override
    public final boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return classMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

}
