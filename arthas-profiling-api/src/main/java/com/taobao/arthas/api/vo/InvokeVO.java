package com.taobao.arthas.api.vo;

import com.taobao.arthas.api.enums.InvokeType;
import lombok.Getter;

import java.util.Map;

/**
 * 通知点 Created by vlinux on 15/5/20.
 */
@Getter
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

    private final Map<String, Object> attach;

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
            long headInvokeId, long invokeId, Map<String, Object> attach
    ) {
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
        this.attach = attach;
    }

    public static InvokeVO newForBefore(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, InvokeType invokeType
            , long headInvokeId, long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                null, //throwExp
                invokeType, headInvokeId, invokeId, attach);
    }

    public static InvokeVO newForAfterReturning(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Object returnObj, InvokeType invokeType
            , long headInvokeId, long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, returnObj, null, //throwExp
                invokeType, headInvokeId, invokeId, attach);
    }

    public static InvokeVO newForAfterThrowing(
            ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] params, Throwable throwExp, InvokeType invokeType
            , long headInvokeId, long invokeId, Map<String, Object> attach
    ) {
        return new InvokeVO(loader, clazz, methodName, methodArgumentTypes, target, params, null, //returnObj
                throwExp, invokeType, headInvokeId, invokeId, attach);
    }

    @Override
    public String toString() {
        return headInvokeId + "-" + invokeId + " | " + invokeType + " | " + clazz.getName() + "#" + methodName + "(" + String.join(",", methodArgumentTypes) + ")";
    }

}
