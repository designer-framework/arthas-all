package com.taobao.arthas.core.properties;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.interceptor.SimpleSpyInterceptorApi;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-28 13:54
 */
@Data
@NoArgsConstructor
public class MethodInvokeWatchProperties {

    /**
     * 全限定方法名
     * fullyQualifiedMethodName
     */
    private String method;

    /**
     * 是否允许重新加载已被装载的类
     */
    private Boolean canRetransform = Boolean.FALSE;

    private Class<? extends SpyInterceptorApi> interceptor = SimpleSpyInterceptorApi.class;

    /**
     * 只能继承该类
     */
    private Class<? extends SimpleMethodInvokePointcutAdvisor> pointcutAdvisor = SimpleMethodInvokePointcutAdvisor.class;

    public MethodInvokeWatchProperties(String method, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor) {
        this(method, canRetransform, interceptor, SimpleMethodInvokePointcutAdvisor.class);
    }

    public MethodInvokeWatchProperties(String method, Boolean canRetransform, Class<? extends SpyInterceptorApi> interceptor, Class<? extends SimpleMethodInvokePointcutAdvisor> pointcutAdvisor) {
        this.method = method;
        this.canRetransform = canRetransform;
        this.interceptor = interceptor;
        this.pointcutAdvisor = pointcutAdvisor;
    }

    public ClassMethodInfo getMethodInfo() {
        return ClassMethodInfo.create(method);
    }

    @Override
    public String toString() {
        return "{\"method\": \"" + method + '\"' +
                ", \"canRetransform\": \"" + canRetransform + "\"" +
                ", \"interceptor\": \"" + interceptor.getName() + "\"" +
                "}";
    }

}
