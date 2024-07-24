package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.cache.Cache;
import com.taobao.arthas.api.pointcut.Pointcut;

/**
 * @description: 切面
 * @author: Designer
 * @date : 2024-07-23 22:40
 */
public interface PointcutAdvisor extends Advisor, Cache {

    /**
     * 切点
     *
     * @return
     */
    Pointcut getPointcut();

    boolean isCached(String className, String methodName, String methodDesc);

}
