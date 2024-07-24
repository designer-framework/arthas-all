package com.taobao.arthas.core.instrument;

import com.alibaba.bytekit.asm.MethodProcessor;
import com.alibaba.bytekit.asm.interceptor.InterceptorProcessor;
import com.alibaba.bytekit.asm.interceptor.parser.DefaultInterceptorClassParser;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.deps.org.objectweb.asm.ClassReader;
import com.alibaba.deps.org.objectweb.asm.Opcodes;
import com.alibaba.deps.org.objectweb.asm.tree.ClassNode;
import com.alibaba.deps.org.objectweb.asm.tree.MethodNode;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.core.interceptor.ExtensionSpyInterceptor;
import com.taobao.arthas.core.utils.ByteKitUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

@Slf4j
public class EnhanceProfilingInstrumentTransformer implements ClassFileTransformer {

    private final List<PointcutAdvisor> pointcutAdvisors;

    public EnhanceProfilingInstrumentTransformer(List<PointcutAdvisor> pointcutAdvisors) {
        this.pointcutAdvisors = pointcutAdvisors;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className == null) {
            return null;
        }

        if (className.startsWith("java") || className.startsWith("sun") || className.startsWith("com/sun") || className.startsWith("jdk") //不处理java包
                || className.startsWith("com/taobao/arthas") //不处理arthas包
                || className.startsWith("com/intellij") || className.startsWith("org/jetbrains") //不处理IDEA包
                || AsmUtils.isEnhancerByCGLIB(className) // 不处理cglib类
        ) {
            return null;
        }

        ClassNode classNode = new ClassNode(Opcodes.ASM9);
        ClassReader classReader = AsmUtils.toClassNode(classfileBuffer, classNode);
        // fix https://github.com/alibaba/one-java-agent/issues/51
        classNode = AsmUtils.removeJSRInstructions(classNode);

        //包路径转类名
        String newClassName = ByteKitUtils.normalizeClassName(classNode.name);

        //确保只被增强一次
        boolean enhance = false;
        for (PointcutAdvisor pointcutAdvisor : pointcutAdvisors) {

            Pointcut pointcut = pointcutAdvisor.getPointcut();

            //类名不匹配
            if (!pointcut.isCandidateClass(newClassName)) {
                continue;
            }

            // 查找 @Instrument 字节码里的 method，如果在原来的有同样的，则处理替换；如果没有，则复制过去
            for (MethodNode methodNode : classNode.methods) {

                //调用频率最高的判断放前面, 减少匹配次数
                // 不处理 abstract函数, native, 构造函数, 静态函数。 (按需调整)
                if (AsmUtils.isAbstract(methodNode) || AsmUtils.isNative(methodNode) || AsmUtils.isConstructor(methodNode) || AsmUtils.isStatic(methodNode)) {
                    continue;
                }

                //方法名不匹配则直接匹配下一个方法
                if (!pointcut.isCandidateMethod(newClassName, methodNode.name, methodNode.desc)) {
                    continue;
                }

                //只增强一次
                if (enhance) {
                    continue;
                } else {
                    enhance = true;
                }

                MethodProcessor methodProcessor = new MethodProcessor(classNode, methodNode);

                DefaultInterceptorClassParser processors = new DefaultInterceptorClassParser();
                List<InterceptorProcessor> interceptorProcessors = processors.parse(ExtensionSpyInterceptor.class);

                //匹配成功，则进行字节码替换处理
                for (InterceptorProcessor processor : interceptorProcessors) {

                    try {
                        processor.process(methodProcessor);
                    } catch (Exception e) {
                        log.error(
                                "Class: {}, Method: {}, InterceptorProcessor: {}", newClassName, methodNode.name, processor.getClass().getName()
                                , e
                        );
                    }

                }

            }

        }

        //需要进行字节码增强
        if (enhance) {
            return AsmUtils.toBytes(classNode, loader, classReader);
        } else {
            //无需增强
            return null;
        }
    }

}
