package com.taobao.arthas.core;

import com.alibaba.bytekit.asm.instrument.InstrumentConfig;
import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.asm.matcher.SimpleClassMatcher;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.taobao.arthas.core.instrument.ClassLoader_Instrument;
import com.taobao.arthas.core.instrument.EnhanceProfilingInstrumentTransformer;
import com.taobao.arthas.core.properties.ArthasClassLoaderProperties;
import com.taobao.arthas.core.transformer.TransformerManager;
import com.taobao.arthas.core.utils.FeatureCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;

import java.arthas.SpyAPI;
import java.io.File;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.jar.JarFile;


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

    private SpringProfilingContainer springProfilingContainer;

    private InstrumentTransformer classLoaderInstrumentTransformer;

    private Thread shutdown;

    private ArthasBootstrap(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        this.instrumentation = instrumentation;

        transformerManager = new TransformerManager(instrumentation);

        initFastjson();

        // 1. initSpy()
        initSpy();

        // 2. 启动性能分析容器
        initProfilingContainer(args);

        // 3. 增强ClassLoader
        enhanceClassLoader();

        // 4. 增强待分析的类
        enhanceProfilingClass();

        // 5. hooker
        shutdown = new Thread("spring-profiling-shutdown-hooker") {

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

        Map<String, String> argsMap = FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toMap(args);
        // 给配置全加上前缀
        Map<String, String> mapWithPrefix = new HashMap<String, String>(argsMap.size());
        for (Entry<String, String> entry : argsMap.entrySet()) {
            mapWithPrefix.put("arthas." + entry.getKey(), entry.getValue());
        }
        return getInstance(instrumentation, mapWithPrefix);
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

    private void enhanceProfilingClass() {
        //获取Spy实现类
        SpyAPI.setSpy(springProfilingContainer.getSpyAPI());
        EnhanceProfilingInstrumentTransformer enhanceProfilingInstrumentTransformer = new EnhanceProfilingInstrumentTransformer(springProfilingContainer.getPointcutAdvisor());
        instrumentation.addTransformer(enhanceProfilingInstrumentTransformer, true);
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
        ArthasClassLoaderProperties arthasClassLoaderProperties = springProfilingContainer.getArthasClassLoaderProperties();
        if (arthasClassLoaderProperties.getEnhanceLoaders() == null) {
            return;
        }
        Set<String> loaders = new HashSet<>();

        for (String enhanceLoader : arthasClassLoaderProperties.getEnhanceLoaders()) {
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

            for (Class<?> clazz : instrumentation.getAllLoadedClasses()) {

                if (loaders.contains(clazz.getName())) {
                    try {
                        instrumentation.retransformClasses(clazz);
                    } catch (Throwable e) {
                        //log.error("retransformClasses class error, name: {}", clazz.getName(), e);
                    }
                }

            }

        }

    }

    private void initProfilingContainer(Map<String, String> argsMap) {
        ConfigurableApplicationContext configurableApplicationContext = SpringProfilingContainer.instance(argsMap);
        log.error("性能分析容器启动完毕： {}, ClassLoader: {}", configurableApplicationContext.getDisplayName(), configurableApplicationContext.getClassLoader());
        springProfilingContainer = configurableApplicationContext.getBean(SpringProfilingContainer.class);
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
