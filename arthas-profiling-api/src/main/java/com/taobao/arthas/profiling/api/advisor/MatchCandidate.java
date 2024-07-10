package com.taobao.arthas.profiling.api.advisor;

public interface MatchCandidate {

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
     * @param methodArgTypes
     * @return
     */
    boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes);

}
