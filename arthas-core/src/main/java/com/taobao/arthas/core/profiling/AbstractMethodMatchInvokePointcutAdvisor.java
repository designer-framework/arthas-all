package com.taobao.arthas.core.profiling;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import lombok.Getter;

@Getter
public abstract class AbstractMethodMatchInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor {

    protected final ClassMethodInfo classMethodInfo;

    /**
     * @param fullyQualifiedMethodName 切入点的全限定方法名
     */
    public AbstractMethodMatchInvokePointcutAdvisor(String fullyQualifiedMethodName) {
        this(FullyQualifiedClassUtils.parserClassMethodInfo(fullyQualifiedMethodName));
    }

    /**
     * 方法的切入点信息
     *
     * @param classMethodInfo
     */
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
