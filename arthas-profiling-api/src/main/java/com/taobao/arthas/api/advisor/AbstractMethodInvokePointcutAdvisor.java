package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.interceptor.InvokeInterceptorAdapter;
import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.vo.InvokeVO;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
public abstract class AbstractMethodInvokePointcutAdvisor extends InvokeInterceptorAdapter implements PointcutAdvisor, ClassMethodMatchPointcut, Advice {

    private static final Object none = new Object();

    protected final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public final boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        String cacheKey = getCacheKey(className, methodName, methodDesc);
        if (cache.containsKey(cacheKey)) {

            return true;

        } else {

            if (isCandidateMethod0(className, methodName, methodDesc)) {
                cache.put(cacheKey, none);
                return true;
            }

        }

        return false;
    }

    @Override
    public final boolean isCached(String className, String methodName, String methodDesc) {
        return cache.containsKey(getCacheKey(className, methodName, methodDesc));
    }

    public String getCacheKey(String className, String methodName, String methodDesc) {
        return className + "#" + methodName + methodDesc;
    }

    /**
     * 是否候选方法
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    public abstract boolean isCandidateMethod0(String className, String methodName, String methodDesc);

    public boolean isReady() {
        return true;
    }

    protected abstract void atBefore(InvokeVO invokeVO);

    @Override
    public void before(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atBefore(invokeVO);
        }
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atAfterReturning(invokeVO);
        }
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) throws Throwable {
        if (isReady()) {
            atAfterThrowing(invokeVO);
        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected void atAfterThrowing(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected abstract void atExit(InvokeVO invokeVO);

    @Override
    public Pointcut getPointcut() {
        return this;
    }

    @Override
    public Advice getAdvice() {
        return this;
    }

}
