package com.taobao.arthas.api.handler;

import com.taobao.arthas.api.vo.InvokeVO;

public interface MethodInvokeListener {

    void before(InvokeVO invokeVO);

    void afterReturning(InvokeVO invokeVO);

    void afterThrowing(InvokeVO invokeVO);

}
