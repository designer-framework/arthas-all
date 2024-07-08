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
import com.taobao.arthas.core.advisor.SpringSpyInterceptors;
import com.taobao.arthas.core.container.SpringContainer;
import com.taobao.arthas.core.container.matcher.Matcher;

import java.arthas.SpyAPI;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class SpringInstrumentTransformer implements ClassFileTransformer {

    static {
        SpyAPI.setSpy(new SpyAPI.AbstractSpy() {
            @Override
            public void atEnter(Class<?> clazz, String methodInfo, Object target, Object[] args) {
            }

            @Override
            public void atExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Object returnObject) {

            }

            @Override
            public void atExceptionExit(Class<?> clazz, String methodInfo, Object target, Object[] args, Throwable throwable) {

            }

            @Override
            public void atBeforeInvoke(Class<?> clazz, String invokeInfo, Object target) {

            }

            @Override
            public void atAfterInvoke(Class<?> clazz, String invokeInfo, Object target) {

            }

            @Override
            public void atInvokeException(Class<?> clazz, String invokeInfo, Object target, Throwable throwable) {

            }
        });
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
        Matcher matcher = SpringContainer.getBean(Matcher.class);
        if (!matcher.klass(classNode)) {
            return null;
        }

        // 不处理cglib
        if (AsmUtils.isEnhancerByCGLIB(className)) {
            return null;
        }

        DefaultInterceptorClassParser processors = new DefaultInterceptorClassParser();
        List<InterceptorProcessor> interceptorProcessors = processors.parse(SpringSpyInterceptors.class);

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
                if (!matcher.method(classNode, methodNode)) {
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
