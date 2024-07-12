package com.taobao.arthas.profiling.api.processor;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;

import java.arthas.SpyAPI;
import java.util.Collection;

public interface ProfilingAdaptor {

    SpyAPI.AbstractSpy getSpyAPI();

    Collection<MatchCandidate> getMatchCandidates();

    void addShutdownHook(Runnable runnable);

}
