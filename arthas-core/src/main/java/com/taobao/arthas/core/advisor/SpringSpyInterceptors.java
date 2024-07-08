package com.taobao.arthas.core.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.*;

import java.arthas.SpyAPI;

/**
 * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyTraceExcludeJDKInterceptor1
 * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyTraceExcludeJDKInterceptor2
 * @see com.taobao.arthas.core.advisor.SpyInterceptors.SpyTraceExcludeJDKInterceptor3
 */
public class SpringSpyInterceptors {

    @AtInvoke(name = "", inline = true, whenComplete = false, excludes = "java.**")
    public static void onInvoke(@Binding.This Object target, @Binding.Class Class<?> clazz,
                                @Binding.InvokeInfo String invokeInfo) {
        SpyAPI.atBeforeInvoke(clazz, invokeInfo, target);
    }

    @AtInvoke(name = "", inline = true, whenComplete = true, excludes = "java.**")
    public static void onInvokeAfter(@Binding.This Object target, @Binding.Class Class<?> clazz,
                                     @Binding.InvokeInfo String invokeInfo) {
        SpyAPI.atAfterInvoke(clazz, invokeInfo, target);
    }

    @AtInvokeException(name = "", inline = true, excludes = "java.**")
    public static void onInvokeException(@Binding.This Object target, @Binding.Class Class<?> clazz,
                                         @Binding.InvokeInfo String invokeInfo, @Binding.Throwable Throwable throwable) {
        SpyAPI.atInvokeException(clazz, invokeInfo, target, throwable);
    }

}
