package com.taobao.arthas.core.container.handler;

import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.taobao.arthas.core.container.advisor.SpringAdvice;

public abstract class InvokeAdviceHandlerAdapter implements InvokeAdviceHandler {

    @Override
    public boolean isCandidateClass(ClassNode classNode) {
        return false;
    }

    @Override
    public boolean isCandidateMethod(ClassNode classNode, MethodNode methodNode) {
        return false;
    }

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
