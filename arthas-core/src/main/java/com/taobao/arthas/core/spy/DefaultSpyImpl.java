package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.spy.SpyExtensionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class DefaultSpyImpl extends AbstractSpy {

    @Autowired
    private SpyExtensionApi spyExtensionApi;

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        spyExtensionApi.atEnter(clazz, methodName, methodDesc, target, args);
    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        spyExtensionApi.atExit(clazz, methodName, methodDesc, target, args, returnObject);
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        spyExtensionApi.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable);
    }

}
