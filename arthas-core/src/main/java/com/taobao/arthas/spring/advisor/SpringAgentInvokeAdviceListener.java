package com.taobao.arthas.spring.advisor;

import com.taobao.arthas.profiling.api.enums.InvokeType;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
@Component
public class SpringAgentInvokeAdviceListener extends SpringAdviceListenerAdapter {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    @Autowired
    private List<InvokeAdviceHandler> invokeAdviceHandlers;

    @Override
    public long id() {
        return headInvokeId();
    }

    public long headInvokeId() {
        return invokeStack.get().headInvokeId();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {
        InvokeStack stack = invokeStack.get();

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.pushInvokeId(currInvokeId);
        long headInvokeId = headInvokeId();

        invokeAdviceHandlers.forEach(invokeAdviceHandler -> {

            invokeAdviceHandler.before(
                    InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId)
            );

        });
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();


        invokeAdviceHandlers.forEach(invokeAdviceHandler -> {

            invokeAdviceHandler.afterReturning(
                    InvokeVO.newForAfterReturning(loader, clazz, methodName, methodArgumentTypes, target, args, returnObject, InvokeType.EXIT, headInvokeId, currInvokeId)
            );

        });
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        invokeAdviceHandlers.forEach(invokeAdviceHandler -> {

            invokeAdviceHandler.afterThrowing(
                    InvokeVO.newForAfterThrowing(loader, clazz, methodName, methodArgumentTypes, target, args, throwable, InvokeType.EXCEPTION, headInvokeId, currInvokeId)
            );

        });
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
