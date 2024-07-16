package com.taobao.arthas.profiling.api.processor;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;

import java.arthas.SpyAPI;
import java.util.List;

public interface ProfilingContainer {

    SpyAPI.AbstractSpy getSpyAPI();

    List<MatchCandidate> getMatchCandidates();

    void addShutdownHook(Runnable runnable);

}
