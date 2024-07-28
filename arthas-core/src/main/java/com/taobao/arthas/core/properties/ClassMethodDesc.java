package com.taobao.arthas.core.properties;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;
import com.taobao.arthas.api.vo.ClassMethodInfo;
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
public class ClassMethodDesc {

    /**
     * 全限定方法名
     * fullyQualifiedMethodName
     */
    private String method;

    /**
     * 是否允许重新加载已被装载的类
     */
    private Boolean canRetransform = Boolean.FALSE;

    private Class<? extends SpyInterceptorApi> spyInterceptorApiClass = SimpleSpyInterceptorApi.class;

    public ClassMethodDesc(String method, Boolean canRetransform, Class<? extends SpyInterceptorApi> spyInterceptorApiClass) {
        this.method = method;
        this.canRetransform = canRetransform;
        this.spyInterceptorApiClass = spyInterceptorApiClass;
    }

    public ClassMethodInfo getMethodInfo() {
        return ClassMethodInfo.create(method);
    }

    @Override
    public String toString() {
        return "{\"method\": \"" + method + '\"' +
                ", \"canRetransform\": \"" + canRetransform + "\"}";
    }

}
