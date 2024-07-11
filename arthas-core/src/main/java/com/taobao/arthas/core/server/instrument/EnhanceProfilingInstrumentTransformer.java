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
import com.taobao.arthas.profiling.api.processor.ProfilingAdaptor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.Collection;
import java.util.List;

public class EnhanceProfilingInstrumentTransformer implements ClassFileTransformer {

    private final Logger logger = Loggers.getLogger(getClass());

    private final ProfilingAdaptor profilingAdaptor;

    public EnhanceProfilingInstrumentTransformer(ProfilingAdaptor profilingAdaptor) {
        this.profilingAdaptor = profilingAdaptor;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }

        //不切arthas包
        if (className.startsWith("java") || className.startsWith("javax") || className.startsWith("sun")) {
            return null;
        }

        //
        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
        // fix https://github.com/alibaba/one-java-agent/issues/51
        classNode = AsmUtils.removeJSRInstructions(classNode);

        //类不匹配
        String newClassName = StringUtils.normalizeClassName(classNode.name);

        if (!isCandidateClass(newClassName)) {
            return null;
        }

        // 不处理cglib
        if (AsmUtils.isEnhancerByCGLIB(className)) {
            return null;
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

            //方法不匹配
            if (!isCandidateMethod(newClassName, methodNode)) {
                continue;
            }

            MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);

            //匹配上，则进行字节码替换处理
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


        }

        return AsmUtils.toBytes(classNode, loader, classReader);
    }

    /**
     * @param newClassName
     * @return
     */
    private boolean isCandidateClass(String newClassName) {
        Collection<MatchCandidate> matchCandidates = profilingAdaptor.getMatchCandidates();
        for (MatchCandidate matchCandidate : matchCandidates) {
            if (matchCandidate.isCandidateClass(newClassName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param newClassName
     * @param methodNode
     * @return
     */
    private boolean isCandidateMethod(String newClassName, MethodNode methodNode) {

        String[] methodArgumentTypes = StringUtils.getMethodArgumentTypes(methodNode.desc);

        Collection<MatchCandidate> matchCandidates = profilingAdaptor.getMatchCandidates();

        for (MatchCandidate matchCandidate : matchCandidates) {
            if (matchCandidate.isCandidateMethod(newClassName, methodNode.name, methodArgumentTypes)) {
                return true;
            }
        }
        return false;

    }

}
