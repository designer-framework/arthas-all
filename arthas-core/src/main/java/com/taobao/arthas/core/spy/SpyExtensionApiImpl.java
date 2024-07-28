package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.enums.InvokeType;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.api.vo.ByteKitUtils;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <pre>
 * 怎么从 className|methodDesc 到 id 对应起来？？
 * 当id少时，可以id自己来判断是否符合？
 *
 * 如果是每个 className|methodDesc 为 key ，是否
 * </pre>
 *
 * @author hengyunabc 2020-04-24
 */
@Slf4j
public class SpyExtensionApiImpl implements SpyExtensionApi {

    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    private final AtomicLong ID_GENERATOR = new AtomicLong(0);

    private final List<PointcutAdvisor> pointcutAdvisors;

    public SpyExtensionApiImpl(List<PointcutAdvisor> pointcutAdvisors) {
        this.pointcutAdvisors = pointcutAdvisors;
    }

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Map<String, Object> attach) {
        InvokeStack stack = invokeStack.get();

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.pushInvokeId(currInvokeId);
        long headInvokeId = stack.headInvokeId();

        proceed(
                clazz, methodName, methodDesc, InvokeType.ENTER
                , advice ->
                        advice.before(InvokeVO.newForBefore(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, InvokeType.ENTER
                                , headInvokeId, currInvokeId, attach
                        ))
        );
    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject, Map<String, Object> attach) {
        InvokeStack stack = invokeStack.get();
        long headInvokeId = stack.headInvokeId();
        long currInvokeId = stack.popInvokeId();

        proceed(
                clazz, methodName, methodDesc, InvokeType.EXIT
                , advice ->
                        advice.afterReturning(InvokeVO.newForAfterReturning(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, returnObject, InvokeType.EXIT
                                , headInvokeId, currInvokeId, attach
                        ))
        );
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable, Map<String, Object> attach) {
        InvokeStack stack = invokeStack.get();
        long headInvokeId = stack.headInvokeId();
        long currInvokeId = stack.popInvokeId();

        proceed(
                clazz, methodName, methodDesc, InvokeType.EXIT
                , advice ->
                        advice.afterThrowing(InvokeVO.newForAfterThrowing(
                                clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, throwable, InvokeType.EXCEPTION
                                , headInvokeId, currInvokeId, attach
                        ))
        );
    }

    /**
     * @param clazz
     * @param methodName
     * @param methodDesc
     * @param invokeType
     * @param invokeConsumer
     */
    private void proceed(Class<?> clazz, String methodName, String methodDesc, InvokeType invokeType, Consumer<Advice> invokeConsumer) {
        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                Pointcut pointcut = pointcutAdvisor.getPointcut();
                if (pointcut.isHit(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    invokeConsumer.accept(advice);

                }

            }

        } catch (Throwable e) {
            log.error("{} -> 异常, Class:{}, Method: {}", invokeType, clazz.getName(), methodName, e);
        }
    }

    interface Consumer<T> {
        void accept(T o) throws Throwable;
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
