package com.taobao.arthas.core.advisor;

/**
 * @see Advice
 */
public class SpringAdvice {

    private final ClassLoader loader;
    private final Class<?> clazz;
    private final ArthasMethod method;
    private final Object target;
    private final Object[] params;
    private final Object returnObj;
    private final Throwable throwExp;
    private final boolean isBefore;
    private final boolean isThrow;
    private final boolean isReturn;
    private final long processorId;
    private final long invokeId;

    public long getProcessorId() {
        return processorId;
    }

    public long getInvokeId() {
        return invokeId;
    }

    public boolean isBefore() {
        return isBefore;
    }

    public boolean isAfterReturning() {
        return isReturn;
    }

    public boolean isAfterThrowing() {
        return isThrow;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public Object getTarget() {
        return target;
    }

    public Object[] getParams() {
        return params;
    }

    public Object getReturnObj() {
        return returnObj;
    }

    public Throwable getThrowExp() {
        return throwExp;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public ArthasMethod getMethod() {
        return method;
    }

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
    private SpringAdvice(
            ClassLoader loader,
            Class<?> clazz,
            ArthasMethod method,
            Object target,
            Object[] params,
            Object returnObj,
            Throwable throwExp,
            int access,
            long processorId,
            long invokeId
    ) {
        this.loader = loader;
        this.clazz = clazz;
        this.method = method;
        this.target = target;
        this.params = params;
        this.returnObj = returnObj;
        this.throwExp = throwExp;
        isBefore = (access & AccessPoint.ACCESS_BEFORE.getValue()) == AccessPoint.ACCESS_BEFORE.getValue();
        isThrow = (access & AccessPoint.ACCESS_AFTER_THROWING.getValue()) == AccessPoint.ACCESS_AFTER_THROWING.getValue();
        isReturn = (access & AccessPoint.ACCESS_AFTER_RETUNING.getValue()) == AccessPoint.ACCESS_AFTER_RETUNING.getValue();
        this.processorId = processorId;
        this.invokeId = invokeId;
    }

    public static SpringAdvice newForBefore(ClassLoader loader,
                                            Class<?> clazz,
                                            ArthasMethod method,
                                            Object target,
                                            Object[] params,
                                            long processorId,
                                            long invokeId) {
        return new SpringAdvice(
                loader,
                clazz,
                method,
                target,
                params,
                null, //returnObj
                null, //throwExp
                AccessPoint.ACCESS_BEFORE.getValue()
                , processorId, invokeId
        );
    }

    public static SpringAdvice newForAfterReturning(ClassLoader loader,
                                                    Class<?> clazz,
                                                    ArthasMethod method,
                                                    Object target,
                                                    Object[] params,
                                                    Object returnObj,
                                                    long processorId,
                                                    long invokeId) {
        return new SpringAdvice(
                loader,
                clazz,
                method,
                target,
                params,
                returnObj,
                null, //throwExp
                AccessPoint.ACCESS_AFTER_RETUNING.getValue()
                , processorId, invokeId
        );
    }

    public static SpringAdvice newForAfterThrowing(ClassLoader loader,
                                                   Class<?> clazz,
                                                   ArthasMethod method,
                                                   Object target,
                                                   Object[] params,
                                                   Throwable throwExp,
                                                   long processorId,
                                                   long invokeId) {
        return new SpringAdvice(
                loader,
                clazz,
                method,
                target,
                params,
                null, //returnObj
                throwExp,
                AccessPoint.ACCESS_AFTER_THROWING.getValue()
                , processorId, invokeId
        );

    }

}
