package com.taobao.arthas.api.interceptor;

import com.taobao.arthas.api.advice.Advice;

/**
 * 方法调用拦截器<br/>
 * Created by vlinux on 15/5/17.
 */
public interface InvokeInterceptor extends Advice {

    /**
     * 监听器创建<br/>
     * 监听器被注册时触发
     */
    void create();

    /**
     * 监听器销毁<br/>
     * 监听器被销毁时触发
     */
    void destroy();

}
