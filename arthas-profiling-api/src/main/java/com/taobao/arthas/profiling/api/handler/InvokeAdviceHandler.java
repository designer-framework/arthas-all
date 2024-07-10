package com.taobao.arthas.profiling.api.handler;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.vo.InvokeVO;

public interface InvokeAdviceHandler extends MatchCandidate {

    void before(InvokeVO invokeVO);

    void afterReturning(InvokeVO invokeVO);

    void afterThrowing(InvokeVO invokeVO);

}
