package com.taobao.arthas.plugin.core.profiling.bean;

import com.alibaba.bytekit.asm.binding.Binding;
import com.alibaba.bytekit.asm.interceptor.annotation.AtExceptionExit;
import com.alibaba.bytekit.asm.interceptor.annotation.AtInvoke;
import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentRootInitializedEvent;
import com.taobao.arthas.plugin.core.profiling.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

import java.arthas.SpyAPI;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InitializingSingletonsPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements DisposableBean, InitializingBean, AgentLifeCycleHook {

    private static final String beanName = "beanName";

    public InitializingSingletonsPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo, Class<? extends SpyInterceptorApi> interceptor) {
        super(componentEnum, classMethodInfo, interceptor);
    }

    @Override
    public void start() {
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(
                this, InitializedComponent.root(SpringComponentEnum.SMART_INITIALIZING_SINGLETON, BigDecimal.ZERO, true)
        ));
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getAttach().get(beanName));
    }

    public static class AfterSingletonsInstantiatedSpyInterceptorApi implements SpyInterceptorApi {

        @AtInvoke(whenComplete = false, inline = true, name = "afterSingletonsInstantiated")
        public static void atEnter(
                @Binding.This Object target, @Binding.Class Class<?> clazz, @Binding.MethodName String methodName, @Binding.MethodDesc String methodDesc, @Binding.Args Object[] args
                , @Binding.LocalVars Object[] vars, @Binding.LocalVarNames String[] varNames
        ) {
            if (varNames != null && varNames.length > 0) {
                Map<String, Object> attach = new HashMap<>();
                for (int i = 0; i < varNames.length; i++) {
                    attach.put(varNames[i], vars[i]);
                }
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, attach);
            } else {
                SpyAPI.atEnter(clazz, methodName, methodDesc, target, args, Collections.emptyMap());
            }
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
