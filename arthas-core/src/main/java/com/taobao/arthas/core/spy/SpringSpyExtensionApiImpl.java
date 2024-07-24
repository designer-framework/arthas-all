package com.taobao.arthas.core.spy;

import com.taobao.arthas.api.advice.Advice;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.api.spy.SpyExtensionApi;
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
    public void atEnter(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args) {

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                Pointcut pointcut = pointcutAdvisor.getPointcut();

                if (pointcut.isCandidateClass(clazz.getName()) && pointcut.isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.before(clazz, methodName, methodArgumentTypes, target, args);

                }

            }

        } catch (Throwable e) {
            log.error("AtEnter异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Object returnObject) {

        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                Pointcut pointcut = pointcutAdvisor.getPointcut();

                if (pointcut.isCandidateClass(clazz.getName()) && pointcut.isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.afterReturning(clazz, methodName, methodArgumentTypes, target, args, returnObject);

                }

            }

        } catch (Throwable e) {
            log.error("AfterReturning异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }

    }

    @Override
    public void atExceptionExit(Class<?> clazz, String methodName, String[] methodArgumentTypes, Object target, Object[] args, Throwable throwable) {
        try {

            for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

                Pointcut pointcut = pointcutAdvisor.getPointcut();

                if (pointcut.isCandidateClass(clazz.getName()) && pointcut.isCandidateMethod(clazz.getName(), methodName, methodArgumentTypes)) {

                    Advice advice = pointcutAdvisor.getAdvice();
                    advice.afterThrowing(clazz, methodName, methodArgumentTypes, target, args, throwable);

                }

            }

        } catch (Throwable e) {
            log.error("AtExceptionExit异常, Class:{}, Method: {}", clazz.getName(), methodName, e);
        }
    }

}
