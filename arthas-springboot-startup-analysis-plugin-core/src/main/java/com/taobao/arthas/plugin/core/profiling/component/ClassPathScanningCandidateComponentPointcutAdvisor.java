package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentChildInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ClassPathScanningCandidateComponentPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor implements AgentLifeCycleHook {

    private final SpringAgentStatistics springAgentStatistics;

    /**
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    public ClassPathScanningCandidateComponentPointcutAdvisor(SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo, SpringAgentStatistics springAgentStatistics) {
        super(springComponentEnum, classMethodInfo);
        this.springAgentStatistics = springAgentStatistics;
    }

    @Override
    protected String childItemName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[0]);
    }

    /**
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#ClassPathScanningCandidateComponentProvider(boolean)
     */
    @Override
    public void start() {
        super.start();

        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        CompletableFuture.runAsync(() -> {

            try {
                for (StackTraceElement stack : stackTrace) {
                    if ("main".equals(stack.getMethodName())) {

                        Class<? extends Annotation> aspectClass = (Class<? extends Annotation>) Class.forName("org.aspectj.lang.annotation.Aspect", true, contextClassLoader);

                        //
                        if (aspectClass.isAnnotation()) {

                            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
                            scanner.addIncludeFilter(new AnnotationTypeFilter(aspectClass));
                            scanner.setResourceLoader(new DefaultResourceLoader(ClassUtils.getDefaultClassLoader()));

                            List<InitializedComponent.Children> children = findBasePackages(stack.getClassName())
                                    .stream().map(s_package -> {
                                        return scanner.findCandidateComponents("com");
                                    })
                                    .flatMap(Collection::stream)
                                    .map(beanDefinition -> {
                                        return InitializedComponent.child(SpringComponentEnum.CLASS_PATH_SCANNING, beanDefinition.getBeanClassName(), BigDecimal.ZERO);
                                    })
                                    .collect(Collectors.toList());
                            applicationEventPublisher.publishEvent(new ComponentChildInitializedEvent(this, children));

                        }

                        break;
                    }
                }

            } catch (Exception e) {
                //ignore
                log.info("Ignore Aspectj scanner", e);
            }
        });

    }

    /**
     * 可能会报错, 但大概率不会...
     *
     * @param
     * @return
     */
    @SneakyThrows
    private List<String> findBasePackages(String springApplicationClassStr) {
        //
        Method findMergedAnnotationAttributesMethod = ClassUtils.getMethod(
                Class.forName("org.springframework.core.annotation.AnnotatedElementUtils", true, Thread.currentThread().getContextClassLoader())
                , "findMergedAnnotationAttributes"
                , AnnotatedElement.class, String.class, boolean.class, boolean.class
        );

        //
        Class<?> springApplicationClass = Class.forName(springApplicationClassStr, true, Thread.currentThread().getContextClassLoader());
        LinkedHashMap<String, Object> mergedAnnotationAttributes = (LinkedHashMap<String, Object>) ReflectionUtils.invokeMethod(
                findMergedAnnotationAttributesMethod, null
                , springApplicationClass, "org.springframework.context.annotation.ComponentScan", false, true
        );

        //
        String[] basePackages = (String[]) mergedAnnotationAttributes.get("basePackages");

        Arrays.stream(basePackages).map(string -> {
            basePackages.length
        })


        return waitScan.entrySet().stream()
                .map(entry -> {
                    return entry.getValue().stream().map(string -> entry.getKey() + "." + entry.getValue()).collect(Collectors.toSet());
                })
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

}
