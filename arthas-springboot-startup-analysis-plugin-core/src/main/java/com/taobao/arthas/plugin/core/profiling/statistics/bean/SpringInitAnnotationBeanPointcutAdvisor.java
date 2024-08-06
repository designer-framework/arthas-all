package com.taobao.arthas.plugin.core.profiling.statistics.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.events.BeanInitMethodInvokeLifeCycleEvent;
import com.taobao.arthas.plugin.core.events.ComponentRootInitializedEvent;
import com.taobao.arthas.plugin.core.profiling.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.BeanLifeCycleDuration;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.SneakyThrows;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;

import java.arthas.SpyAPI;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;

/**
 * @see org.springframework.beans.factory.InitializingBean
 * @see javax.annotation.PostConstruct
 * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)
 * @see
 */
public class SpringInitAnnotationBeanPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements DisposableBean, InitializingBean {

    /**
     * @param componentEnum
     * @param classMethodInfo
     * @param interceptor
     * @see org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor.LifecycleMetadata#invokeInitMethods(java.lang.Object, java.lang.String)
     */
    public SpringInitAnnotationBeanPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        super.start();
        applicationEventPublisher.publishEvent(
                new ComponentRootInitializedEvent(this, InitializedComponent.root(getComponentEnum(), BigDecimal.ZERO, true))
        );
    }

    @Override
    public boolean isReady(InvokeVO invokeVO) {
        return super.isReady(invokeVO) && invokeVO.getAttach().get("initMethods") != null;
    }

    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

        //记录耗时
        BeanLifeCycleDuration beanLifeCycleDuration = BeanLifeCycleDuration.create(String.valueOf(invokeVO.getAttach().get("initMethods")), methodInvokeVO);
        applicationEventPublisher.publishEvent(
                new BeanInitMethodInvokeLifeCycleEvent(this, String.valueOf(invokeVO.getParams()[1]), beanLifeCycleDuration)
        );
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[1]);
    }

    public static class InitMethodSpyInterceptorApi implements SpyInterceptorApi {

        @SneakyThrows
        @AtEnter(inline = true)
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Field(name = "initMethods") Collection<?> initMethods
        ) {

            if (!initMethods.isEmpty()) {

                Map<String, Object> attach = new HashMap<>();
                attach.put("initMethods", "true");
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);

            } else {

                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, Collections.emptyMap());

            }

        }

        @SneakyThrows
        @AtExit(inline = true)
        public static void atExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Return Object returnObj
                //Collection<org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor$LifecycleElement> initMethods
                , @Binding.Field(name = "initMethods") Collection<?> initMethods
        ) {

            if (!initMethods.isEmpty()) {

                List<String> methods = new LinkedList<>();
                for (Object lifecycleElement : initMethods) {

                    /**
                     * @see InitDestroyAnnotationBeanPostProcessor.LifecycleElement#getMethod()
                     */
                    Method getMethod = lifecycleElement.getClass().getMethod("getMethod");
                    Method initMethod = (Method) getMethod.invoke(lifecycleElement);
                    methods.add(initMethod.getName());

                }

                Map<String, Object> attach = new HashMap<>();
                attach.put("initMethods", args[1] + "#" + String.join(",", methods));

                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, attach);

            } else {

                SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, Collections.emptyMap());

            }

        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Throwable Throwable throwable
        ) {
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, Collections.emptyMap());
        }

    }

}
