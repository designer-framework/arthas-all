package com.taobao.arthas.api.vo;

import com.taobao.arthas.api.enums.InvokeType;

/**
 * 通知点 Created by vlinux on 15/5/20.
 */
public class InvokeVO {

    private final ClassLoader loader;
    private final Class<?> clazz;
    private final Object target;
    private final Object[] params;
    private final Object returnObj;
    private final Throwable throwExp;
    private final InvokeType invokeType;

    //将ArthasMethod的字段直接放到当前类
    private final String methodName;

    private final String[] methodArgumentTypes;

    //将调用链路相关的id直接放到当前类
    private final long headInvokeId;

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
    protected InvokeVO(
            ClassLoader loader,
            Class<?> clazz,
            String methodName, String[] methodArgumentTypes,
            Object target,
            Object[] params,
            Object returnObj,
            Throwable throwExp,
            InvokeType invokeType,
            long headInvokeId, long invokeId) {
        this.loader = loader;
        this.clazz = clazz;
        this.methodName = methodName;
        this.methodArgumentTypes = methodArgumentTypes;
        this.target = target;
        this.params = params;
        this.returnObj = returnObj;
        this.throwExp = throwExp;
        this.invokeType = invokeType;
        this.headInvokeId = headInvokeId;
        this.invokeId = invokeId;
    }

    public static InvokeVO newForBefore(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, InvokeType invokeType, long headInvokeId, long invokeId) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                null, //throwExp
                invokeType, headInvokeId, invokeId);
    }

    public static InvokeVO newForAfterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Object returnObj, InvokeType invokeType, long headInvokeId, long invokeId) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, returnObj, null, //throwExp
                invokeType, headInvokeId, invokeId);
    }

    public static InvokeVO newForAfterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Throwable throwExp, InvokeType invokeType, long headInvokeId, long invokeId) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                throwExp, invokeType, headInvokeId, invokeId);
    }

    public String[] getMethodArgumentTypes() {
        return methodArgumentTypes;
    }

    public ClassLoader getLoader() {
        return loader;
    }

    public Class<?> getClazz() {
        return clazz;
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

    public InvokeType getInvokeType() {
        return invokeType;
    }

    public String getMethodName() {
        return methodName;
    }

    public String[] getMethodDesc() {
        return methodArgumentTypes;
    }

    public long getHeadInvokeId() {
        return headInvokeId;
    }

    public long getInvokeId() {
        return invokeId;
    }

    @Override
    public String toString() {
        return headInvokeId + "-" + invokeId + " | " + invokeType + " | " + clazz.getName() + "#" + methodName + "(" + String.join(",", methodArgumentTypes) + ")";
    }

}
