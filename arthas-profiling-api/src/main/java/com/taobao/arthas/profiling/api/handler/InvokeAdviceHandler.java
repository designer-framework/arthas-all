package com.taobao.arthas.profiling.api.handler;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.vo.InvokeVO;

public interface InvokeAdviceHandler extends MatchCandidate {

    void handler(InvokeVO invokeVO);

}
