package com.taobao.arthas.core.container.matcher;

import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;

public interface Matcher {

    boolean klass(ClassNode classNode);

    boolean method(ClassNode classNode, MethodNode methodNode);

}
