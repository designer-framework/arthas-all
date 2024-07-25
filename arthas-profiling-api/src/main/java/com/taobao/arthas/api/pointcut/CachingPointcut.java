package com.taobao.arthas.api.pointcut;

import com.taobao.arthas.api.vo.ByteKitUtils;
import com.taobao.arthas.api.vo.ClassMethodInfo;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-23 22:30
 */
public class CachingPointcut implements Pointcut {

    private static final Object none = new Object();

    protected Map<String, Object> cache = new ConcurrentHashMap<>();

    @Getter
    protected ClassMethodInfo classMethodInfo;

    protected boolean canRetransform;

    public CachingPointcut(ClassMethodInfo classMethodInfo) {
        this(classMethodInfo, Boolean.FALSE);
    }

    public CachingPointcut(ClassMethodInfo classMethodInfo, Boolean canRetransform) {
        this.classMethodInfo = classMethodInfo;
        this.canRetransform = canRetransform;
    }

    public boolean getCanRetransform() {
        return canRetransform;
    }

    @Override
    public boolean isCandidateClass(String className) {
        return classMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String methodDesc) {
        String cacheKey = getCacheKey(className, methodName, methodDesc);
        if (cache.containsKey(cacheKey)) {

            return true;

        } else {

            if (isCandidateMethod0(className, methodName, methodDesc)) {
                cache.put(cacheKey, none);
                return true;
            }

        }

        return false;
    }

    /**
     * 是否候选方法
     *
     * @param className
     * @param methodName
     * @param methodDesc
     * @return
     */
    protected boolean isCandidateMethod0(String className, String methodName, String methodDesc) {
        return classMethodInfo.isCandidateMethod(methodName, ByteKitUtils.getMethodArgumentTypes(methodDesc));
    }

    @Override
    public boolean isHit(String className, String methodName, String methodDesc) {
        return cache.containsKey(getCacheKey(className, methodName, methodDesc));
    }

    protected String getCacheKey(String className, String methodName, String methodDesc) {
        return className + "#" + methodName + methodDesc;
    }

}
