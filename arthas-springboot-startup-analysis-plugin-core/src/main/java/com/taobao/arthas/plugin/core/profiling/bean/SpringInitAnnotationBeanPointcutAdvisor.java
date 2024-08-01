package com.taobao.arthas.plugin.core.profiling.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.events.BeanInitMethodInvokeEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.arthas.SpyAPI;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @see org.springframework.beans.factory.InitializingBean
 * @see javax.annotation.PostConstruct
 */
public class SpringInitAnnotationBeanPointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    public static final String lifecycleMetadataCache = "lifecycleMetadataCache";

    public static final String emptyLifecycleMetadata = "emptyLifecycleMetadata";

    private final ThreadLocal<Stack<BeanInitMethodInvokeEvent>> eventThreadLocal = ThreadLocal.withInitial(Stack::new);

    /**
     * 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        eventThreadLocal.get().push(new BeanInitMethodInvokeEvent(this, String.valueOf(invokeVO.getParams()[1])));
    }

    /**
     * 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        if (!eventThreadLocal.get().isEmpty()) {
            BeanInitMethodInvokeEvent invokeEvent = eventThreadLocal.get().pop();
            invokeEvent.instantiated();
            if (invokeEvent.getDuration().compareTo(BigDecimal.valueOf(10)) > 0) {
                applicationEventPublisher.publishEvent(invokeEvent);
            }
        }
    }

    @Override
    public void destroy() {
        eventThreadLocal.remove();
    }

    public static class InitMethodSpyInterceptorApi implements SpyInterceptorApi {

        @AtEnter(inline = true)
        public static void atEnter(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Field(name = "lifecycleMetadataCache") Object lifecycleMetadataCache, @Binding.Field(name = "emptyLifecycleMetadata") Object emptyLifecycleMetadata
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put(SpringInitAnnotationBeanPointcutAdvisor.lifecycleMetadataCache, lifecycleMetadataCache);
            attach.put(SpringInitAnnotationBeanPointcutAdvisor.emptyLifecycleMetadata, emptyLifecycleMetadata);
            SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
        }

        @AtExit(inline = true)
        public static void atExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Return Object returnObj) {
            SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, null);
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Throwable Throwable throwable) {
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, null);
        }

    }

}
