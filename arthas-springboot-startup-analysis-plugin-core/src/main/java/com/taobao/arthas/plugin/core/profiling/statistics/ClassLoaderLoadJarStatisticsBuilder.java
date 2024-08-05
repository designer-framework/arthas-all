package com.taobao.arthas.plugin.core.profiling.statistics;

import com.alibaba.bytekit.utils.ClassLoaderUtils;
import com.taobao.arthas.plugin.core.vo.SimpleStatisticsInfo;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import com.taobao.arthas.plugin.core.vo.StatisticsInfo;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 17:08
 */
public class ClassLoaderLoadJarStatisticsBuilder implements StatisticsBuilder {

    private final Instrumentation instrumentation;

    public ClassLoaderLoadJarStatisticsBuilder(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    public Map<String, Set<String>> getClassLoaderNotLoadedJars(Predicate<ClassLoader> filter) {
        //key: ClassLoader , value: 暂未使用到的jar包
        return getClassLoaderLoadedJars().entrySet().stream()
                .filter(entry -> filter.test(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> {

                    URL[] allJar = ClassLoaderUtils.getUrls(entry.getKey());
                    return Arrays.stream(allJar)
                            .map(URL::toString)
                            .filter(url -> !entry.getValue().contains(url))
                            .collect(Collectors.toSet());

                }));
    }

    private Map<ClassLoader, Set<String>> getClassLoaderLoadedJars() {
        Map<ClassLoader, Set<String>> classLoaderLoadedJars = new HashMap<>();

        for (Class<?> loadedClass : instrumentation.getAllLoadedClasses()) {

            ClassLoader classLoader = loadedClass.getClassLoader();
            if (classLoader == null) {
                continue;
            }

            //排除特殊类加载器
            if (classLoader.toString().contains("DelegatingClassLoader")
                    || classLoader.toString().contains("ExtClassLoader")
                    || classLoader.toString().contains("ArthasClassloader")
            ) {
                continue;
            }

            CodeSource codeSource = loadedClass.getProtectionDomain().getCodeSource();
            if (codeSource == null) {
                continue;
            }

            URL location = codeSource.getLocation();
            //过滤出jar包
            if (location == null || !location.toString().endsWith(".jar")) {
                continue;
            }

            //类加载器 : 该类加载器已加载的jar包
            Set<String> urls = classLoaderLoadedJars.computeIfAbsent(classLoader, ladedJars -> new HashSet<>());
            urls.add(location.toString());
        }

        return classLoaderLoadedJars;
    }

    @Override
    public StatisticsInfo build(SpringAgentStatistics springAgentStatistics) {
        return new SimpleStatisticsInfo("unusedJarMap", getClassLoaderNotLoadedJars(classLoader -> {
            return Thread.currentThread().getContextClassLoader().toString().equals(classLoader.toString());
        }));
    }

}
