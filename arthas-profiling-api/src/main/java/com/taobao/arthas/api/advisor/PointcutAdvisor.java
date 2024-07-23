package com.taobao.arthas.api.advisor;

import com.taobao.arthas.api.pointcut.Pointcut;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:40
 */
public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();

}
