package com.taobao.arthas.core.container.spy;

import com.taobao.arthas.core.advisor.AdviceListener;
import com.taobao.arthas.core.util.StringUtils;

import java.arthas.SpyAPI.AbstractSpy;

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
public class SpringSpyImpl extends AbstractSpy {

    private final AdviceListener adviceListener;

    public SpringSpyImpl(AdviceListener adviceListener) {
        this.adviceListener = adviceListener;
    }

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];
        try {
            adviceListener.before(clazz, methodName, methodDesc, target, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];
        try {
            adviceListener.afterReturning(clazz, methodName, methodDesc, target, args, returnObject);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];
        try {
            adviceListener.afterThrowing(clazz, methodName, methodDesc, target, args, throwable);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    //以下几个方法暂时没用
    @Override
    public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {
    }

    @Override
    public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {
    }

    @Override
    public void atInvokeException(Class<?> clazz, String invokeInfo, Object target, Throwable throwable) {
    }

}
