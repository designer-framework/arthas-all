package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.enums.InvokeType;
import com.taobao.arthas.api.interceptor.InvokeInterceptorAdapter;
import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.vo.InvokeVO;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
public abstract class AbstractMethodInvokePointcutAdvisor extends InvokeInterceptorAdapter implements PointcutAdvisor, ClassMethodMatchPointcut, Advice {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    protected final Set<String> cache = new HashSet<>();

    @Override
    public final boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        String cacheKey = getCacheKey(className, methodName, methodDesc);
        if (cache.contains(getCacheKey(className, methodName, methodDesc))) {

            return true;

        } else {

            if (isCandidateMethod0(methodName, methodName, methodDesc)) {
                cache.add(cacheKey);
                return true;
            }

        }

        return false;
    }

    @Override
    public final boolean isCached(String className, String methodName, String methodDesc) {
        return cache.contains(getCacheKey(className, methodName, methodDesc));
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

    @Override
    public long id() {
        return headInvokeId();
    }

    public long headInvokeId() {
        return invokeStack.get().headInvokeId();
    }

    public boolean isReady() {
        return true;
    }

    @Override
    public final void before(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {
        InvokeStack stack = invokeStack.get();

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.pushInvokeId(currInvokeId);
        long headInvokeId = headInvokeId();

        if (isReady()) {

            InvokeVO invokeVO = InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId);
            atBefore(invokeVO);

        }
    }

    protected abstract void atBefore(InvokeVO invokeVO);

    @Override
    public final void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        if (isReady()) {

            InvokeVO invokeVO = InvokeVO.newForAfterReturning(loader, clazz, methodName, methodArgumentTypes, target, args, returnObject, InvokeType.EXIT, headInvokeId, currInvokeId);
            atAfterReturning(invokeVO);

        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    @Override
    public final void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        if (isReady()) {

            InvokeVO invokeVO = InvokeVO.newForAfterThrowing(loader, clazz, methodName, methodArgumentTypes, target, args, throwable, InvokeType.ENTER, headInvokeId, currInvokeId);
            atAfterThrowing(invokeVO);

        }
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

    static class InvokeStack {

        /**
         * 调用栈
         */
        private final Deque<Long> stack = new LinkedList<>();

        public void pushInvokeId(long invokeId) {
            stack.push(invokeId);
        }

        public long popInvokeId() {
            long invokeId = stack.pop();
            if (stack.isEmpty()) {
                invokeStack.remove();
            }

            return invokeId;
        }

        /**
         * 首次调用
         *
         * @return
         */
        long headInvokeId() {
            return stack.getFirst();
        }

        boolean isEmpty() {
            return stack.isEmpty();
        }

    }

}
