package com.taobao.arthas.spring.advisor;

import com.taobao.arthas.profiling.api.enums.InvokeType;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.properties.ArthasProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 参见
 * {@link com.taobao.arthas.core.command.monitor200.StackAdviceListener}
 */
public class SpringAdviceListener extends SpringAdviceListenerAdapter implements BeanDefinitionRegistryPostProcessor {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);
    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    private final InvokeAdviceHandler invokeAdviceHandler;

    private final ArthasProperties arthasProperties;

    public SpringAdviceListener(
            InvokeAdviceHandler invokeAdviceHandler
            , ArthasProperties arthasProperties
    ) {
        this.invokeAdviceHandler = invokeAdviceHandler;
        this.arthasProperties = arthasProperties;
    }

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

        invokeAdviceHandler.before(
                InvokeVO.newForBefore(loader, clazz, methodName, methodArgumentTypes, target, args, InvokeType.ENTER, headInvokeId, currInvokeId)
        );
    }

    @Override
    public void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        invokeAdviceHandler.afterReturning(
                InvokeVO.newForAfterReturning(loader, clazz, methodName, methodArgumentTypes, target, args, returnObject, InvokeType.ENTER, headInvokeId, currInvokeId)
        );
    }

    @Override
    public void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {

        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        invokeAdviceHandler.afterThrowing(
                InvokeVO.newForAfterThrowing(loader, clazz, methodName, methodArgumentTypes, target, args, throwable, InvokeType.ENTER, headInvokeId, currInvokeId)
        );
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
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
