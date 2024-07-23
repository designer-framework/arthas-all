package com.taobao.arthas.core.advisor;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.enums.InvokeType;
import com.taobao.arthas.api.interceptor.InvokeInterceptorAdapter;
import com.taobao.arthas.api.pointcut.ClassMethodMatchPointcut;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
@Slf4j
@Setter
public abstract class AbstractMethodInvokePointcutAdvisor extends InvokeInterceptorAdapter implements PointcutAdvisor, ClassMethodMatchPointcut, Advice {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    @Override
    public abstract boolean isCandidateClass(String className);

    @Override
    public abstract boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes);

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
    public void before(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {
        InvokeStack stack = invokeStack.get();

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.pushInvokeId(currInvokeId);
        long headInvokeId = headInvokeId();

        if (isReady() && isCandidateClass(clazz.getName()) && isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

            InvokeVO invokeVO = InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId);
            atBefore(invokeVO);

        }
    }

    protected void atBefore(InvokeVO invokeVO) {
        log.info("AtBefore:{}", invokeVO);
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        if (isReady() && isCandidateClass(clazz.getName()) && isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

            InvokeVO invokeVO = InvokeVO.newForAfterReturning(loader, clazz, methodName, methodArgumentTypes, target, args, returnObject, InvokeType.EXIT, headInvokeId, currInvokeId);
            atAfterReturning(invokeVO);

        }
    }

    protected void atAfterReturning(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        if (isReady() && isCandidateClass(clazz.getName()) && isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

            InvokeVO invokeVO = InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId);
            atAfterThrowing(invokeVO);

        }
    }

    protected void atAfterThrowing(InvokeVO invokeVO) {
        atExit(invokeVO);
    }

    protected void atExit(InvokeVO invokeVO) {
        log.info("AtExit:{}", invokeVO);
    }

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
