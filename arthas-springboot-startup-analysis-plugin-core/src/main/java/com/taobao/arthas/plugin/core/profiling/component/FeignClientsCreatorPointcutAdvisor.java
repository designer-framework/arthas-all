package com.taobao.arthas.plugin.core.profiling.component;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

import java.arthas.SpyAPI;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class FeignClientsCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements AgentLifeCycleHook {

    protected final ThreadLocal<InitializedComponent> componentChildren;

    public FeignClientsCreatorPointcutAdvisor(
            SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(classMethodInfo, interceptor);
        this.componentChildren = ThreadLocal.withInitial(() -> InitializedComponent.root(springComponentEnum));
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        componentChildren.get().insertChildren(
                new InitializedComponent.Children(
                        ((Class<?>) getParams(invokeVO)[0]).getName(), invokeDetail.getDuration()
                )
        );
    }

    @Override
    public void stop() {

        InitializedComponent initializedComponent = componentChildren.get();
        if (!initializedComponent.getChildren().isEmpty()) {
            applicationEventPublisher.publishEvent(
                    new ComponentInitializedEvent(this, initializedComponent.updateDurationByChildren())
            );
        }

    }

    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        Map<String, Object> attach = invokeVO.getAttach();
        return new Object[]{((Class<?>) attach.get(FeignClientSpyInterceptorApi.TYPE)).getName()};
    }

    public static class FeignClientSpyInterceptorApi implements SpyInterceptorApi {

        public static final String TYPE = "type";

        @AtEnter(inline = true)
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Field(name = TYPE) Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put(TYPE, type);
            SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
        }

        @AtExit(inline = true)
        public static void atExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Return Object returnObj, @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put(TYPE, type);
            SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, attach);
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Throwable Throwable throwable, @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put(TYPE, type);
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, attach);
        }

    }

}
