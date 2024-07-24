package com.taobao.arthas.core.profiling;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.utils.ByteKitUtils;
import com.taobao.arthas.core.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

@Setter
public abstract class AbstractMethodMatchInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements InitializingBean {

    protected ClassMethodInfo classMethodInfo;

    public AbstractMethodMatchInvokePointcutAdvisor() {
    }

    /**
     * @param fullyQualifiedMethodName 切入点的全限定方法名
     */
    public AbstractMethodMatchInvokePointcutAdvisor(String fullyQualifiedMethodName) {
        classMethodInfo = FullyQualifiedClassUtils.parserClassMethodInfo(fullyQualifiedMethodName);
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
    }

}
