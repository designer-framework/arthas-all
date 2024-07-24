package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.enums.InvokeType;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.utils.ByteKitUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
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
@Component
public class SpringSpyExtensionApiImpl implements SpyExtensionApi {

    private static final AtomicLong ID_GENERATOR = new AtomicLong(0);

    /**
     * 调用链
     */
    private static final ThreadLocal<InvokeStack> invokeStack = ThreadLocal.withInitial(InvokeStack::new);

    @Autowired
    private List<PointcutAdvisor> pointcutAdvisors;

    protected long headInvokeId() {
        return invokeStack.get().headInvokeId();
    }

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        InvokeStack stack = invokeStack.get();

        long currInvokeId = ID_GENERATOR.addAndGet(1);
        stack.pushInvokeId(currInvokeId);
        long headInvokeId = headInvokeId();

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();

                    advice.before(
                            InvokeVO.newForBefore(clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, InvokeType.ENTER, headInvokeId, currInvokeId)
                    );

                }

            }

        } catch (Throwable e) {
            log.error("AtEnter异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();

                    advice.afterReturning(
                            InvokeVO.newForAfterReturning(clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, returnObject, InvokeType.EXIT, headInvokeId, currInvokeId)
                    );

                }

            }

        } catch (Throwable e) {
            log.error("AfterReturning异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        InvokeStack stack = invokeStack.get();
        long headInvokeId = headInvokeId();
        long currInvokeId = stack.popInvokeId();

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();

                    advice.afterThrowing(
                            InvokeVO.newForAfterThrowing(clazz.getClassLoader(), clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, throwable, InvokeType.ENTER, headInvokeId, currInvokeId)
                    );

                }

            }

        } catch (Throwable e) {
            log.error("AtExceptionExit异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }
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
