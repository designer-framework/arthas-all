package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.pointcut.Pointcut;

/**
 * @description: 切面
 * @author: Designer
 * @date : 2024-07-23 22:40
 */
public interface PointcutAdvisor extends Advisor {

    /**
     * 切点
     *
     * @return
     */
    Pointcut getPointcut();

    /**
     * 是增强类(该方法直接读取缓存, 不需要再次解析类信息)
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    boolean isHit(String className, String methodName, String methodDesc);

}
