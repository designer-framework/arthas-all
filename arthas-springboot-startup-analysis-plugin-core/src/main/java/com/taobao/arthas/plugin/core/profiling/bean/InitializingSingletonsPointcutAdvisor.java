package com.taobao.arthas.plugin.core.profiling.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtInvoke;
import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.events.SmartInstantiateSingletonEvent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.arthas.SpyAPI;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class InitializingSingletonsPointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements DisposableBean, InitializingBean {

    private static final String beanName = "beanName";

    private final ThreadLocal<Stack<SmartInstantiateSingletonEvent>> eventThreadLocal = ThreadLocal.withInitial(Stack::new);

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        eventThreadLocal.get().push(new SmartInstantiateSingletonEvent(this, String.valueOf(invokeVO.getAttach().get(beanName))));
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        //正在加载SmartInitializingSingleton Bean
        if (!eventThreadLocal.get().isEmpty()) {
            SmartInstantiateSingletonEvent singletonEvent = eventThreadLocal.get().pop();
            singletonEvent.instantiated();
            applicationEventPublisher.publishEvent(singletonEvent);
        }
    }

    @Override
    public void destroy() {
        eventThreadLocal.remove();
    }

    public static class AfterSingletonsInstantiatedSpyInterceptorApi implements SpyInterceptorApi {

        @AtInvoke(whenComplete = false, inline = true, name = "afterSingletonsInstantiated")
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.LocalVars Object[] vars, @Binding.LocalVarNames String[] varNames
        ) {
            Map<String, Object> attach = new HashMap<>();
            for (int i = 0; i < varNames.length; i++) {
                attach.put(varNames[i], vars[i]);
            }
            SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
        }

        @AtInvoke(whenComplete = true, inline = true, name = "afterSingletonsInstantiated")
        public static void atExit
                (@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                        , @Binding.LocalVars Object[] vars, @Binding.LocalVarNames String[] varNames
                ) {
            SpyAPI.atExit(clazz, methodName, methodDesc, target, args, null, null);
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(@Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args, @Binding.Throwable Throwable throwable) {
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, null);
        }

    }

}
