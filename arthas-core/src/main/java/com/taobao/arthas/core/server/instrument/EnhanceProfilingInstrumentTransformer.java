package com.taobao.arthas.core.server.instrument;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.log.Logger;
import com.alibaba.bytekit.log.Loggers;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.taobao.arthas.core.advisor.ExtensionSpyInterceptor;
import com.taobao.arthas.core.util.StringUtils;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class EnhanceProfilingInstrumentTransformer implements ClassFileTransformer {

    private final Logger logger = Loggers.getLogger(getClass());

    private final List<MatchCandidate> matchCandidates;

    public EnhanceProfilingInstrumentTransformer(List<MatchCandidate> matchCandidates) {
        this.matchCandidates = matchCandidates;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }

        //不处理java包
        if (className.startsWith("java") || className.startsWith("javax") || className.startsWith("sun") || className.startsWith("jdk")) {
            return null;
        }

        //不处理arthas包
        if (className.startsWith("com/taobao/arthas") || className.startsWith("com/intellij") || className.startsWith("org/jetbrains")) {
            return null;
        }

        // 不处理cglib
        if (AsmUtils.isEnhancerByCGLIB(className)) {
            return null;
        }

        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
        // fix https://github.com/alibaba/one-java-agent/issues/51
        classNode = AsmUtils.removeJSRInstructions(classNode);

        String newClassName = StringUtils.normalizeClassName(classNode.name);

        for (MatchCandidate matchCandidate : matchCandidates) {

            //类名不匹配
            if (!matchCandidate.isCandidateClass(newClassName)) {
                continue;
            }

            DefaultInterceptorClassParser processors = new DefaultInterceptorClassParser();
            List<InterceptorProcessor> interceptorProcessors = processors.parse(ExtensionSpyInterceptor.class);

            // 查找 @Instrument 字节码里的 method，如果在原来的有同样的，则处理替换；如果没有，则复制过去
            for (MethodNode methodNode : classNode.methods) {

                //调用频率最高的判断放前面, 减少匹配次数
                // 不处理abstract函数
                if (AsmUtils.isAbstract(methodNode)) {
                    continue;
                }

                // 不处理native
                if (AsmUtils.isNative(methodNode)) {
                    continue;
                }

                // 不处理构造函数
                if (AsmUtils.isConstructor(methodNode)) {
                    continue;
                }

                //方法不匹配则直接匹配下一个方法
                if (!matchCandidate.isCandidateMethod(newClassName, methodNode.name, StringUtils.getMethodArgumentTypes(methodNode.desc))) {
                    continue;
                }

                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);

                //匹配成功，则进行字节码替换处理
                for (InterceptorProcessor processor : interceptorProcessors) {

                    try {
                        processor.process(methodProcessor);
                    } catch (Exception e) {
                        logger.error(
                                "Class: {}, Method: {}, InterceptorProcessor: {}", newClassName, methodNode.name, processor.getClass().getName()
                                , e
                        );
                    }

                }

                //只需要增强一次
                return AsmUtils.toBytes(classNode, loader, classReader);

            }

        }

        //无需增强
        return null;
    }

}
