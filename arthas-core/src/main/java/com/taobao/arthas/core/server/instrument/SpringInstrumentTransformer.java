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
import com.taobao.arthas.core.spring.SpringContainer;
import com.taobao.arthas.core.util.StringUtils;
import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.interceptor.SpyInterceptorExtensionApi;

import java.arthas.SpyAPI;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class SpringInstrumentTransformer implements ClassFileTransformer {

    static {
        /**
         * 从容器中获取Spy实现类
         */
        SpyAPI.setSpy(SpringContainer.getBean(SpyAPI.AbstractSpy.class));
    }

    private final Logger logger = Loggers.getLogger(getClass());

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        //
        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
        // fix https://github.com/alibaba/one-java-agent/issues/51
        classNode = AsmUtils.removeJSRInstructions(classNode);

        //类不匹配
        MatchCandidate matchCandidate = SpringContainer.getBean(MatchCandidate.class);
        if (!matchCandidate.isCandidateClass(classNode.name)) {
            return null;
        }

        // 不处理cglib
        if (AsmUtils.isEnhancerByCGLIB(className)) {
            return null;
        }

        DefaultInterceptorClassParser processors = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> interceptorProcessors = processors.parse(SpringContainer.getBean(SpyInterceptorExtensionApi.class).getClass());

        //匹配上，则进行字节码替换处理
        for (InterceptorProcessor processor : interceptorProcessors) {

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
                if (!matchCandidate.isCandidateMethod(classNode.name, methodNode.name, StringUtils.getMethodArgumentTypes(methodNode.desc))) {
                    return null;
                }

                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);
                try {
                    processor.process(methodProcessor);
                } catch (Exception e) {
                    logger.error(
                            "Class: {}, Method: {}, InterceptorProcessor: {}", classNode.name, methodNode.name, processor.getClass().getName()
                            , e
                    );
                }

            }

        }

        return AsmUtils.toBytes(classNode, loader, classReader);
    }

}
