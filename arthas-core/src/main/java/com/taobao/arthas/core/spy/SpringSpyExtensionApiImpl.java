package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.spy.SpyExtensionApi;
import com.taobao.arthas.core.utils.ByteKitUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Component
public class SpringSpyExtensionApiImpl implements SpyExtensionApi {

    @Autowired
    private List<PointcutAdvisor> pointcutAdvisors;

    @Override
    public void atEnter(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args) {

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.before(clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args);

                }

            }

        } catch (Throwable e) {
            log.error("AtEnter异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Object returnObject) {

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.afterReturning(clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, returnObject);

                }

            }

        } catch (Throwable e) {
            log.error("AfterReturning异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String methodDesc, Object target, Object[] args, Throwable throwable) {
        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                if (pointcutAdvisor.isCached(clazz.getName(), methodName, methodDesc)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.afterThrowing(clazz, methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc), target, args, throwable);

                }

            }

        } catch (Throwable e) {
            log.error("AtExceptionExit异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }
    }

}
