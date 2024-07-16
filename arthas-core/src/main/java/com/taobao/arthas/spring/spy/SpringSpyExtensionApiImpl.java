package com.taobao.arthas.spring.spy;

import com.taobao.arthas.profiling.api.advisor.AdviceListener;
import com.taobao.arthas.profiling.api.spy.SpyExtensionApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
@Component
public class SpringSpyExtensionApiImpl implements SpyExtensionApi {

    @Autowired
    private List<AdviceListener> adviceListeners;

    @Override
    public void atEnter(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {
        try {
            for (AdviceListener adviceListener : adviceListeners) {
                adviceListener.before(clazz, methodName, methodArgumentTypes, target, args);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {
        try {
            for (AdviceListener adviceListener : adviceListeners) {
                adviceListener.afterReturning(clazz, methodName, methodArgumentTypes, target, args, returnObject);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) {
        try {
            for (AdviceListener adviceListener : adviceListeners) {
                adviceListener.afterThrowing(clazz, methodName, methodArgumentTypes, target, args, throwable);
            }
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
