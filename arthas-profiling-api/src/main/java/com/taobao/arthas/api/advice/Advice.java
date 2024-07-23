package com.taobao.arthas.api.advice;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:34
 */
public interface Advice {

    /**
     * 前置通知
     *
     * @param clazz               类
     * @param methodName          方法名
     * @param methodArgumentTypes 方法描述
     * @param target              目标类实例
     *                            若目标为静态方法,则为null
     * @param args                参数列表
     * @throws Throwable 通知过程出错
     */
    void before(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) throws Throwable;

    /**
     * 返回通知
     *
     * @param clazz               类
     * @param methodName          方法名
     * @param methodArgumentTypes 方法描述
     * @param target              目标类实例
     *                            若目标为静态方法,则为null
     * @param args                参数列表
     * @param returnObject        返回结果
     *                            若为无返回值方法(void),则为null
     * @throws Throwable 通知过程出错
     */
    void afterReturning(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) throws Throwable;

    /**
     * 异常通知
     *
     * @param clazz               类
     * @param methodName          方法名
     * @param methodArgumentTypes 方法描述
     * @param target              目标类实例
     *                            若目标为静态方法,则为null
     * @param args                参数列表
     * @param throwable           目标异常
     * @throws Throwable 通知过程出错
     */
    void afterThrowing(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable;

}
