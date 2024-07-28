package com.taobao.arthas.plugin.core.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import lombok.extern.slf4j.Slf4j;

import java.arthas.SpyAPI;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring项目启动耗时分析
 */
@Slf4j
public class FeignClientsCreatorPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    public FeignClientsCreatorPointcutAdvisor() {
        super(
                ClassMethodInfo.create("org.springframework.cloud.openfeign.FeignClientFactoryBean#getTarget()")
                , FeignClientSpyInterceptorApi.class
        );
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
                , @Binding.Field(name = "type") Object type
        ) throws NoSuchFieldException {
            Map<String, Object> attach = new HashMap<>();
            attach.put("type", type);
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
