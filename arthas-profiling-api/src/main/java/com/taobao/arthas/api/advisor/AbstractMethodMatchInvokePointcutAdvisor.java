package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.vo.ByteKitUtils;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;


public abstract class AbstractMethodMatchInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements InitializingBean {

    protected ClassMethodInfo classMethodInfo;

    public AbstractMethodMatchInvokePointcutAdvisor() {
    }

    /**
     * @param fullyQualifiedMethodName 切入点的全限定方法名
     */
    public AbstractMethodMatchInvokePointcutAdvisor(String fullyQualifiedMethodName) {
        this(ClassMethodInfo.create(fullyQualifiedMethodName));
    }

    public AbstractMethodMatchInvokePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        this.classMethodInfo = classMethodInfo;
    }

    /**
     * 该参数不能为空
     *
     * @param classMethodInfo
     */
    public void setClassMethodInfo(ClassMethodInfo classMethodInfo) {
        this.classMethodInfo = classMethodInfo;
    }

    @Override
    public final boolean isCandidateClass(String className) {
        return classMethodInfo.isCandidateClass(className);
    }

    @Override
    public final boolean isCandidateMethod0(String className, String methodName, String methodDesc) {
        return classMethodInfo.isCandidateMethod(methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(classMethodInfo, "ClassMethodInfo");
        Assert.notNull(agentState, "AgentState");
    }

}
