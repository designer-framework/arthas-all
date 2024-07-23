package com.taobao.arthas.api.interceptor;

/**
 * 参见
 * {@link com.taobao.arthas.core.AdviceListenerAdapter}
 */
public abstract class InvokeInterceptorAdapter implements InvokeInterceptor {

    /**
     * 方法每次调用都会+1
     *
     * @return
     */
    @Override
    public abstract long id();

    @Override
    public void create() {
        //ignore
    }

    @Override
    final public void before(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) throws Throwable {
        before(clazz.getClassLoader(), clazz, methodName, methodArgumentTypes, target, args);
    }

    @Override
    final public void afterReturning(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) throws Throwable {
        afterReturning(clazz.getClassLoader(), clazz, methodName, methodArgumentTypes, target, args, returnObject);
    }

    @Override
    final public void afterThrowing(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable {
        afterThrowing(clazz.getClassLoader(), clazz, methodName, methodArgumentTypes, target, args, throwable);
    }

    /**
     * 前置通知
     *
     * @param loader 类加载器
     * @param clazz  类
     * @param method 方法
     * @param target 目标类实例 若目标为静态方法,则为null
     * @param args   参数列表
     * @throws Throwable 通知过程出错
     */
    public abstract void before(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) throws Throwable;

    /**
     * 返回通知
     *
     * @param loader       类加载器
     * @param clazz        类
     * @param method       方法
     * @param target       目标类实例 若目标为静态方法,则为null
     * @param args         参数列表
     * @param returnObject 返回结果 若为无返回值方法(void),则为null
     * @throws Throwable 通知过程出错
     */
    public abstract void afterReturning(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) throws Throwable;

    /**
     * 异常通知
     *
     * @param loader    类加载器
     * @param clazz     类
     * @param method    方法
     * @param target    目标类实例 若目标为静态方法,则为null
     * @param args      参数列表
     * @param throwable 目标异常
     * @throws Throwable 通知过程出错
     */
    public abstract void afterThrowing(ClassLoader loader, Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) throws Throwable;

    @Override
    public void destroy() {
        //ignore
    }

}
