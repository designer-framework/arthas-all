package com.taobao.arthas.api.advice;

import com.taobao.arthas.api.enums.InvokeType;
import com.taobao.arthas.api.interceptor.InvokeInterceptorAdapter;
import com.taobao.arthas.api.vo.InvokeVO;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
public class DefaultInvokeInterceptor extends InvokeInterceptorAdapter {

    private final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 调用链
     */
    private final InvokeStack invokeStack = new InvokeStack();

    @Override
    public long id() {
        return headInvokeId();
    }

    public long headInvokeId() {
        return invokeStack.headInvokeId();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        invokeStack.pushInvokeId(currInvokeId);
        long headInvokeId = headInvokeId();

        atBefore(InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId));
    }

    public void atBefore(InvokeVO invokeVO) {
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        long headInvokeId = headInvokeId();
        long currInvokeId = invokeStack.popInvokeId();


        atAfterReturning(InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId));
    }

    public void atAfterReturning(InvokeVO invokeVO) {
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {

        long headInvokeId = headInvokeId();
        long currInvokeId = invokeStack.popInvokeId();

        atAfterThrowing(InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId));
    }

    public void atAfterThrowing(InvokeVO invokeVO) {
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
            return stack.pop();
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
