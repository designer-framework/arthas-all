package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.advice.Advice;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:39
 */
public interface Advisor {

    /**
     * 切入点调用
     *
     * @return
     */
    Advice getAdvice();

}
