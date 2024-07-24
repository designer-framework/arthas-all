package com.taobao.arthas.api.pointcut;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
public interface Pointcut {

    /**
     * 是否候选类
     *
     * @param className
     * @return
     */
    boolean isCandidateClass(String className);

    /**
     * 是否候选方法
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    boolean isCandidateMethod(String className, String methodName, String methodDesc);

}
