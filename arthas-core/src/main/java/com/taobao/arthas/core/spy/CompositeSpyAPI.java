package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.spy.SpyExtensionApi;

import java.arthas.SpyAPI.AbstractSpy;
import java.util.List;

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
public class CompositeSpyAPI extends AbstractSpy {

    private final List<SpyExtensionApi> spyExtensionApis;

    public CompositeSpyAPI(List<SpyExtensionApi> spyExtensionApis) {
        this.spyExtensionApis = spyExtensionApis;
    }

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {
        for (SpyExtensionApi spyExtensionApi : spyExtensionApis) {
            spyExtensionApi.atEnter(clazz, methodName, methodDesc, target, args);
        }
    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {
        for (SpyExtensionApi spyExtensionApi : spyExtensionApis) {
            spyExtensionApi.atExit(clazz, methodName, methodDesc, target, args, returnObject);
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        for (SpyExtensionApi spyExtensionApi : spyExtensionApis) {
            spyExtensionApi.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable);
        }
    }

}