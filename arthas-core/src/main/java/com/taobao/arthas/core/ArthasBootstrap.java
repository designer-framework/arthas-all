package com.taobao.arthas.core;

import com.alibaba.bytekit.asm.instrument.InstrumentConfig;
import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.asm.matcher.SimpleClassMatcher;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.taobao.arthas.api.advisor.PointcutAdvisor;
import com.taobao.arthas.api.pointcut.Pointcut;
import com.taobao.arthas.core.instrument.ClassLoader_Instrument;
import com.taobao.arthas.core.instrument.EnhanceProfilingInstrumentTransformer;
import com.taobao.arthas.core.properties.AgentClassLoaderProperties;
import com.taobao.arthas.core.transformer.TransformerManager;
import com.taobao.arthas.core.utils.FeatureCodec;
import com.taobao.arthas.core.utils.InstrumentationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.arthas.SpyAPI;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;


/**
 * @author vlinux on 15/5/2.
 * @author hengyunabc
 */
@Slf4j
public class ArthasBootstrap {

    private static final String ARTHAS_SPY_JAR = "arthas-spy.jar";

    private static ArthasBootstrap arthasBootstrap;

    private final TransformerManager transformerManager;

    private final Instrumentation instrumentation;

    private final Thread shutdown;

    private AgentContainer agentContainer;

    private InstrumentTransformer classLoaderInstrumentTransformer;

    private ArthasBootstrap(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        this.instrumentation = instrumentation;

        transformerManager = new TransformerManager(instrumentation);

        // 初始化序列化工具
        initFastjson();

        // 1. initSpy()
        initSpy();

        // 2. 启动性能分析容器
        initProfilingContainer(args);

        // 4. 增强ClassLoader
        enhanceClassLoader();

        // 5. 增强待分析的类
        enhanceNormalClass();

        // 6. hooker
        shutdown = new Thread("spring-agent-shutdown-hooker") {

            @Override
            public void run() {
                ArthasBootstrap.this.destroy();
            }
        };

        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @return ArthasServer单例
     * @throws Throwable
     */
    public synchronized static ArthasBootstrap getInstance(Instrumentation instrumentation, String args) throws Throwable {
        if (arthasBootstrap != null) {
            return arthasBootstrap;
        }
        //将Agent命令行参数解析成Map格式
        return getInstance(instrumentation, FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toMap(args));
    }

    /**
     * 单例
     *
     * @param instrumentation JVM增强
     * @return ArthasServer单例
     * @throws Throwable
     */
    public synchronized static ArthasBootstrap getInstance(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        if (arthasBootstrap == null) {
            arthasBootstrap = new ArthasBootstrap(instrumentation, args);
        }
        return arthasBootstrap;
    }

    private void enhanceNormalClass() {
        //获取Spy实现类
        SpyAPI.setSpy(agentContainer.getSpyAPI());

        EnhanceProfilingInstrumentTransformer enhanceProfilingInstrumentTransformer = new EnhanceProfilingInstrumentTransformer(agentContainer.getPointcutAdvisor());
        instrumentation.addTransformer(enhanceProfilingInstrumentTransformer, true);

        //
        InstrumentationUtils.trigerRetransformClasses(
                instrumentation
                , agentContainer.getPointcutAdvisor().stream()
                        .map(PointcutAdvisor::getPointcut).filter(Pointcut::getCanRetransform).collect(Collectors.toList())
        );
    }

    private void initFastjson() {
        // disable  fastjson circular reference feature
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        // add date format option for  fastjson
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteDateUseDateFormat.getMask();
        // ignore getter error #1661
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.IgnoreErrorGetter.getMask();
        // #2081
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.WriteNonStringKeyAsString.getMask();
    }

    private void initSpy() throws Throwable {
        // TODO init SpyImpl ?

        // 将Spy添加到BootstrapClassLoader
        ClassLoader parent = ClassLoader.getSystemClassLoader().getParent();
        Class<?> spyClass = null;
        if (parent != null) {
            try {
                spyClass = parent.loadClass("java.arthas.SpyAPI");
            } catch (Throwable e) {
                // ignore
            }
        }

        if (spyClass == null) {

            CodeSource codeSource = ArthasBootstrap.class.getProtectionDomain().getCodeSource();
            if (codeSource != null) {
                File arthasCoreJarFile = new File(codeSource.getLocation().toURI().getSchemeSpecificPart());
                File spyJarFile = new File(arthasCoreJarFile.getParentFile(), ARTHAS_SPY_JAR);
                instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(spyJarFile));
            } else {
                throw new IllegalStateException("can not find " + ARTHAS_SPY_JAR);
            }

        }
    }

    private void enhanceClassLoader() throws IOException, UnmodifiableClassException {
        AgentClassLoaderProperties agentClassLoaderProperties = agentContainer.getAgentClassLoaderProperties();
        if (agentClassLoaderProperties.getEnhanceLoaders() == null) {
            return;
        }
        Set<String> loaders = new HashSet<>();

        for (String enhanceLoader : agentClassLoaderProperties.getEnhanceLoaders()) {
            loaders.add(enhanceLoader.trim());
        }

        // 增强 ClassLoader#loadClsss ，解决一些ClassLoader加载不到 SpyAPI的问题
        // https://github.com/alibaba/arthas/issues/1596
        byte[] classBytes = IOUtils.getBytes(ArthasBootstrap.class.getClassLoader()
                .getResourceAsStream(ClassLoader_Instrument.class.getName().replace('.', '/') + ".class"));

        SimpleClassMatcher matcher = new SimpleClassMatcher(loaders);
        InstrumentConfig instrumentConfig = new InstrumentConfig(AsmUtils.toClassNode(classBytes), matcher);

        InstrumentParseResult instrumentParseResult = new InstrumentParseResult();
        instrumentParseResult.addInstrumentConfig(instrumentConfig);
        classLoaderInstrumentTransformer = new InstrumentTransformer(instrumentParseResult);
        instrumentation.addTransformer(classLoaderInstrumentTransformer, true);

        if (loaders.size() == 1 && loaders.contains(ClassLoader.class.getName())) {

            // 如果只增强 java.lang.ClassLoader，可以减少查找过程
            instrumentation.retransformClasses(ClassLoader.class);

        } else {

            InstrumentationUtils.trigerRetransformClasses(instrumentation, loaders);

        }

    }

    private void initProfilingContainer(Map<String, String> argsMap) {
        ConfigurableApplicationContext configurableApplicationContext = AgentContainer.instance(argsMap);
        log.error("性能分析容器启动完毕： {}, ClassLoader: {}", configurableApplicationContext.getDisplayName(), configurableApplicationContext.getClassLoader());
        agentContainer = configurableApplicationContext.getBean(AgentContainer.class);
    }

    /**
     * call reset() before destroy()
     */
    public void destroy() {

        if (transformerManager != null) {
            transformerManager.destroy();
        }
        if (classLoaderInstrumentTransformer != null) {
            instrumentation.removeTransformer(classLoaderInstrumentTransformer);
        }
        // clear the reference in Spy class.
        cleanUpSpyReference();
        if (shutdown != null) {
            try {
                Runtime.getRuntime().removeShutdownHook(shutdown);
            } catch (Throwable t) {
                // ignore
            }
        }

        //log.info("spring-profiling-server destroy completed.");
    }

    /**
     * 清除SpyAPI里的引用
     */
    private void cleanUpSpyReference() {
        try {
            SpyAPI.setNopSpy();
            SpyAPI.destroy();
        } catch (Throwable e) {
            // ignore
        }
        // AgentBootstrap.resetArthasClassLoader();
        try {
            Class<?> clazz = ClassLoader.getSystemClassLoader().loadClass("com.taobao.arthas.agent334.AgentBootstrap");
            Method method = clazz.getDeclaredMethod("resetArthasClassLoader");
            method.invoke(null);
        } catch (Throwable e) {
            // ignore
        }
    }

}
