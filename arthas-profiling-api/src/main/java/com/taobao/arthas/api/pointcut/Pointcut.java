package com.taobao.arthas.api.pointcut;

import com.taobao.arthas.api.interceptor.SpyInterceptorApi;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
public interface Pointcut {

    Pointcut FALSE = new Pointcut() {
        @Override
        public boolean getCanRetransform() {
            return false;
        }

        @Override
        public boolean isCandidateClass(String className) {
            return false;
        }

        @Override
        public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
            return false;
        }

        @Override
        public boolean isHit(String className, String methodName, String methodDesc) {
            return false;
        }

        @Override
        public Class<? extends SpyInterceptorApi> getSpyInterceptorApiClass() {
            return SpyInterceptorApi.class;
        }

    };

    /**
     * 是否允许重新装载
     *
     * @return
     */
    boolean getCanRetransform();

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

    boolean isHit(String className, String methodName, String methodDesc);

    /**
     * @return
     * @see com.taobao.arthas.core.interceptor.ExtensionSpyInterceptor
     */
    Class<? extends SpyInterceptorApi> getSpyInterceptorApiClass();

}
