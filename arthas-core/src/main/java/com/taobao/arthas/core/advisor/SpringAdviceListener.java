package com.taobao.arthas.core.advisor;

import com.taobao.arthas.core.container.SpringContainer;
import com.taobao.arthas.core.container.handler.InvokeDispatcher;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
public class SpringAdviceListener extends SpringAdviceListenerAdapter {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    @Override
    public long id() {
        return processorId();
    }

    public long processorId() {
        return invokeStack.get().headInvokeId();
    }

    @Override
    public void before(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args) throws Throwable {

        InvokeStack stack = invokeStack.get();
        stack.pushInvokeId(ID_GENERATOR.addAndGet(1));

        SpringAdvice springAdvice = SpringAdvice.newForBefore(loader, clazz, method, target, args, processorId(), ID_GENERATOR.addAndGet(1));

    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Object returnObject) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long invokeId = stack.popInvokeId();

        SpringAdvice springAdvice = SpringAdvice.newForAfterReturning(loader, clazz, method, target, args, returnObject, processorId(), invokeId);

    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] args, Throwable throwable) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long invokeId = stack.popInvokeId();

        SpringAdvice springAdvice = SpringAdvice.newForAfterThrowing(loader, clazz, method, target, args, throwable, processorId(), invokeId);

    }

    private void handlerInvoke(SpringAdvice springAdvice) {
        InvokeDispatcher invokeDispatcher = SpringContainer.getBean(InvokeDispatcher.class);
        //
        invokeDispatcher.handler(springAdvice);
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
