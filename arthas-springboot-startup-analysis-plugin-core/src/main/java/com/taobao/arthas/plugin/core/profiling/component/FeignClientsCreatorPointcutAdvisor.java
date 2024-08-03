package com.taobao.arthas.plugin.core.profiling.component;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import lombok.extern.slf4j.Slf4j;

import java.arthas.SpyAPI;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class FeignClientsCreatorPointcutAdvisor extends AbstractComponentRootCreatorPointcutAdvisor {

    private static final String TYPE = "type";

    public FeignClientsCreatorPointcutAdvisor(
            SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor
    ) {
        super(springComponentEnum, classMethodInfo, interceptor);
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        Map<String, Object> attach = invokeVO.getAttach();
        return ((Class<?>) attach.get(TYPE)).getName();
    }

    @Override
    protected Object[] getParams(InvokeVO invokeVO) {
        Map<String, Object> attach = invokeVO.getAttach();
        return new Object[]{((Class<?>) attach.get(TYPE)).getName()};
    }

    public static class FeignClientSpyInterceptorApi implements SpyInterceptorApi {

        @AtEnter(inline = true)
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Field(name = "type") Object type
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
            attach.put("type", type);
            SpyAPI.atExit(clazz, methodName, methodDesc, target, args, returnObj, attach);
        }

        @AtExceptionExit(inline = true)
        public static void atExceptionExit(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.Throwable Throwable throwable, @Binding.Field(name = "type") Object type
        ) {
            Map<String, Object> attach = new HashMap<>();
            attach.put("type", type);
            SpyAPI.atExceptionExit(clazz, methodName, methodDesc, target, args, throwable, attach);
        }

    }

}
