package com.taobao.arthas.core.container.matcher;

import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;

public interface MatchCandidate {

    /**
     * 是否候选类
     *
     * @param classNode
     * @return
     */
    boolean isCandidateClass(ClassNode classNode);

    /**
     * 是否候选方法
     *
     * @param classNode
     * @param methodNode
     * @return
     */
    boolean isCandidateMethod(ClassNode classNode, MethodNode methodNode);

}
