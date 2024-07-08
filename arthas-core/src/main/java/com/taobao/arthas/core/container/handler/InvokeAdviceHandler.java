package com.taobao.arthas.core.container.handler;

import com.taobao.arthas.core.container.advisor.SpringAdvice;
import com.taobao.arthas.core.container.matcher.MatchCandidate;

public interface InvokeAdviceHandler extends MatchCandidate {

    void handler(SpringAdvice springAdvice);

}
