package com.taobao.arthas.core.server;

import com.alibaba.arthas.deps.ch.qos.logback.classic.LoggerContext;
import com.alibaba.arthas.deps.org.slf4j.Logger;
import com.alibaba.arthas.deps.org.slf4j.LoggerFactory;
import com.alibaba.bytekit.asm.instrument.InstrumentConfig;
import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.asm.matcher.SimpleClassMatcher;
import com.alibaba.bytekit.utils.AsmUtils;
import com.alibaba.bytekit.utils.IOUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.taobao.arthas.common.ArthasConstants;
import com.taobao.arthas.core.advisor.TransformerManager;
import com.taobao.arthas.core.config.FeatureCodec;
import com.taobao.arthas.core.server.instrument.ClassLoader_Instrument;
import com.taobao.arthas.core.server.instrument.EnhanceProfilingInstrumentTransformer;
import com.taobao.arthas.core.util.LogUtil;
import com.taobao.arthas.spring.SpringProfilingContainer;
import com.taobao.arthas.spring.properties.ArthasConfigProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;

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
public class ArthasBootstrap {

    public static final String CONFIG_NAME_PROPERTY = "arthas.config.name";

    public static final String CONFIG_LOCATION_PROPERTY = "arthas.config.location";

    public static final String CONFIG_OVERRIDE_ALL = "arthas.config.overrideAll";

    private static final String ARTHAS_SPY_JAR = "arthas-spy.jar";

    private static ArthasBootstrap arthasBootstrap;

    private static LoggerContext loggerContext;

    private final TransformerManager transformerManager;

    private ConfigurableEnvironment arthasEnvironment;

    private ConfigurableApplicationContext configurableApplicationContext;

    private SpringProfilingContainer springProfilingContainer;

    private ArthasConfigProperties configure;

    private Instrumentation instrumentation;

    private InstrumentTransformer classLoaderInstrumentTransformer;

    private Thread shutdown;

    private File outputPath;

    //private ResultViewResolver resultViewResolver;

    private ArthasBootstrap(Instrumentation instrumentation, Map<String, String> args) throws Throwable {
        this.instrumentation = instrumentation;

        initFastjson();

        // 1. initSpy()
        initSpy();

        // 1.1 启动容器
        initArthasSpringProfilingContainer(args);

        // 2. ArthasEnvironment
        initArthasEnvironment();

        String outputPathStr = configure.getOutputPath();
        if (outputPathStr == null) {
            outputPathStr = ArthasConstants.ARTHAS_OUTPUT;
        }
        outputPath = new File(outputPathStr);

        outputPath.mkdirs();

        // 3. init logger
        loggerContext = LogUtil.initLogger(arthasEnvironment);

        // 4. 增强ClassLoader
        enhanceClassLoader();

        // 4.1 增强Spring
        enhanceProfiling();

        shutdown = new Thread("as-shutdown-hooker") {

            @Override
            public void run() {
                ArthasBootstrap.this.destroy();
            }
        };

        transformerManager = new TransformerManager(instrumentation);

        Runtime.getRuntime().addShutdownHook(shutdown);
    }

    static String reslove(StandardEnvironment arthasEnvironment, String key, String defaultValue) {
        String value = arthasEnvironment.getProperty(key);
        if (value == null) {
            return defaultValue;
        }
        return arthasEnvironment.resolvePlaceholders(value);
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

    /**
     * @return ArthasServer单例
     */
    public static ArthasBootstrap getInstance() {
        if (arthasBootstrap == null) {
            throw new IllegalStateException("ArthasBootstrap must be initialized before!");
        }
        return arthasBootstrap;
    }

    private void enhanceProfiling() {
        /**
         * 获取Spy实现类
         */
        SpyAPI.setSpy(springProfilingContainer.getSpyAPI());
        EnhanceProfilingInstrumentTransformer enhanceProfilingInstrumentTransformer = new EnhanceProfilingInstrumentTransformer(springProfilingContainer.getMatchCandidates());
        instrumentation.addTransformer(enhanceProfilingInstrumentTransformer, true);

        springProfilingContainer.addShutdownHook(() -> {
            SpyAPI.destroy();
            instrumentation.removeTransformer(enhanceProfilingInstrumentTransformer);
        });

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

    void enhanceClassLoader() throws IOException, UnmodifiableClassException {
        if (configure.getEnhanceLoaders() == null) {
            return;
        }
        Set<String> loaders = new HashSet<>();
        for (String enhanceLoader : configure.getEnhanceLoaders()) {
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
                        String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                        //logger.error(errorMsg, e);
                    }
                }
            }

        }
    }

    private void initArthasSpringProfilingContainer(Map<String, String> argsMap) throws IOException {
        configurableApplicationContext = SpringProfilingContainer.instance();
        springProfilingContainer = configurableApplicationContext.getBean(SpringProfilingContainer.class);
    }

    private void initArthasEnvironment() throws IOException {
        if (arthasEnvironment == null) {
            //
            arthasEnvironment = configurableApplicationContext.getEnvironment();

            //
            configure = configurableApplicationContext.getBean(ArthasConfigProperties.class);
        }
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
        logger().info("as-server destroy completed.");
        if (loggerContext != null) {
            loggerContext.stop();
        }
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

    public Instrumentation getInstrumentation() {
        return instrumentation;
    }

    public TransformerManager getTransformerManager() {
        return transformerManager;
    }

    private Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }

}
