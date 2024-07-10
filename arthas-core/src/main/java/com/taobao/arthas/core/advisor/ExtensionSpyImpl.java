package com.taobao.arthas.core.advisor;

import com.taobao.arthas.core.util.StringUtils;
import com.taobao.arthas.profiling.api.spy.SpyExtensionApi;

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
public class ExtensionSpyImpl extends AbstractSpy {

    private final SpyExtensionApi spyExtensionApi;

    public ExtensionSpyImpl(SpyExtensionApi spyExtensionApi) {
        this.spyExtensionApi = spyExtensionApi;
    }

    @Override
    public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {

        String[] methodInfos = StringUtils.splitMethodInfo(methodInfo);
        String methodName = methodInfos[0];
        String methodDesc = methodInfos[1];

        spyExtensionApi.atEnter(clazz, methodName, StringUtils.getMethodArgumentTypes(methodDesc), target, args);

    }

    @Override
    public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {

        String[] info = StringUtils.splitMethodInfo(methodInfo);
        String methodName = info[0];
        String methodDesc = info[1];

        spyExtensionApi.atExit(clazz, methodName, StringUtils.getMethodArgumentTypes(methodDesc), target, args, returnObject);

    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {

        String[] methodInfos = StringUtils.splitMethodInfo(methodInfo);
        String methodName = methodInfos[0];
        String methodDesc = methodInfos[1];

        spyExtensionApi.atExceptionExit(clazz, methodName, StringUtils.getMethodArgumentTypes(methodDesc), target, args, throwable);

    }

    /**
     * 解析入参的JAVA类型
     *
     * @param methodDesc
     * @return
     */
    private String[] getMethodArgumentTypes(String methodDesc) {
        return StringUtils.getMethodArgumentTypes(methodDesc);
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
