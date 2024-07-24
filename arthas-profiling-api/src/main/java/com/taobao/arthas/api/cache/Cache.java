package com.taobao.arthas.api.cache;

public interface Cache {

    boolean isCached(String className, String methodName, String methodDesc);

}
