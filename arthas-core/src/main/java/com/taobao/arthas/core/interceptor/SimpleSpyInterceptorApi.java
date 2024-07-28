package com.taobao.arthas.core.interceptor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtEnter;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExit;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;

import java.arthas.SpyAPI;

public class SimpleSpyInterceptorApi implements SpyInterceptorApi {

    @AtEnter(inline = true)
    public static void atEnter(
            @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
    ) {
        SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, null);
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
