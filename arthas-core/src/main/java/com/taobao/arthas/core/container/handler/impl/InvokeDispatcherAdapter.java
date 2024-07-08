package com.taobao.arthas.core.container.handler.impl;

import com.taobao.arthas.core.advisor.SpringAdvice;
import com.taobao.arthas.core.container.handler.InvokeDispatcher;

public abstract class InvokeDispatcherAdapter implements InvokeDispatcher {

    @Override
    public void handler(SpringAdvice springAdvice) {
        if (springAdvice.isBefore()) {
            atEnter(springAdvice);
            return;
        }
        if (springAdvice.isAfterReturning()) {
            atExit(springAdvice);
            return;

        }
        if (springAdvice.isAfterThrowing()) {
            atExit(springAdvice);
            return;
        }
    }

    protected abstract void atEnter(SpringAdvice springAdvice);

    protected abstract void atExit(SpringAdvice springAdvice);

}
