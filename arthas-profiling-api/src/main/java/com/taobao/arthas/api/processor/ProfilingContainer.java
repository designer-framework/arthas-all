package com.taobao.arthas.api.processor;

import com.taobao.arthas.api.advisor.PointcutAdvisor;

import java.arthas.SpyAPI;
import java.util.List;

public interface ProfilingContainer {

    SpyAPI.AbstractSpy getSpyAPI();

    List<PointcutAdvisor> getPointcutAdvisor();

    void addShutdownHook(Runnable runnable);

}
