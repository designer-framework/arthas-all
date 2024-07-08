package com.taobao.arthas.core.container.advisor;

import com.taobao.arthas.core.advisor.AccessPoint;
import com.taobao.arthas.core.advisor.Advice;
import com.taobao.arthas.core.advisor.ArthasMethod;

/**
 * @see Advice
 */
public class SpringAdvice extends Advice {

    private final long processorId;

    private final long invokeId;

    /**
     * for finish
     *
     * @param loader    类加载器
     * @param clazz     类
     * @param method    方法
     * @param target    目标类
     * @param params    调用参数
     * @param returnObj 返回值
     * @param throwExp  抛出异常
     * @param access    进入场景
     */
    private SpringAdvice(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] params, Object returnObj, Throwable throwExp, int access, long processorId, long invokeId) {
        super(loader, clazz, method, target, params, returnObj, throwExp, access);
        this.processorId = processorId;
        this.invokeId = invokeId;
    }

    public static SpringAdvice newForBefore(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] params, long processorId, long invokeId) {
        return new SpringAdvice(loader, clazz, method, target, params, null, //returnObj
                null, //throwExp
                AccessPoint.ACCESS_BEFORE.getValue(), processorId, invokeId);
    }

    public static SpringAdvice newForAfterReturning(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] params, Object returnObj, long processorId, long invokeId) {
        return new SpringAdvice(loader, clazz, method, target, params, returnObj, null, //throwExp
                AccessPoint.ACCESS_AFTER_RETUNING.getValue(), processorId, invokeId);
    }

    public static SpringAdvice newForAfterThrowing(ClassLoader loader, Class<?> clazz, ArthasMethod method, Object target, Object[] params, Throwable throwExp, long processorId, long invokeId) {
        return new SpringAdvice(loader, clazz, method, target, params, null, //returnObj
                throwExp, AccessPoint.ACCESS_AFTER_THROWING.getValue(), processorId, invokeId);

    }

    public long getProcessorId() {
        return processorId;
    }

    public long getInvokeId() {
        return invokeId;
    }

}
