package com.taobao.arthas.plugin.core.turbo.advisor;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtInvoke;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.MethodInvokeVO;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-01 01:10
 * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(java.lang.Class)
 */
public class ApolloTurboPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    public ApolloTurboPointcutAdvisor(ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(classMethodInfo, interceptor);
    }

    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    public static class TurboSpyInterceptorApi implements SpyInterceptorApi {

        @AtInvoke(inline = true, name = "", whenComplete = false)
        public static Object atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
        ) {
            return null;
        }

    }

}
