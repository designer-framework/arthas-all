package com.taobao.arthas.plugin.core.profiling.bean;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.vo.DurationUtils;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.BeanAopProxyCreatedEvent;
import com.taobao.arthas.plugin.core.events.ComponentRootInitializedEvent;
import com.taobao.arthas.plugin.core.profiling.component.AbstractComponentChildCreatorPointcutAdvisor;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(java.lang.Object, java.lang.String, java.lang.Object)
 */
@Slf4j
public class SpringBeanAopProxyPointcutAdvisor extends AbstractComponentChildCreatorPointcutAdvisor implements AgentLifeCycleHook, DisposableBean, InitializingBean {

    public SpringBeanAopProxyPointcutAdvisor(ComponentEnum componentEnum, ClassMethodInfo classMethodInfo) {
        super(componentEnum, classMethodInfo);
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
    }

    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        //被AOP代理过才发布事件
        if (invokeVO.getReturnObj() != null && !(invokeVO.getReturnObj() == invokeVO.getParams()[0])) {
            super.atMethodInvokeAfter(invokeVO, methodInvokeVO);

            //为Bean添加标签
            applicationEventPublisher.publishEvent(
                    new BeanAopProxyCreatedEvent(this, childName(invokeVO), invokeVO.getReturnObj().getClass().getName(), methodInvokeVO.getStartMillis())
            );
        }
    }

    @Override
    protected String childName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[1]);
    }

    @Override
    public void start() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        InitializedComponent root = InitializedComponent.root(SpringComponentEnum.ABSTRACT_AUTO_PROXY_CREATOR, BigDecimal.ZERO, true);
        applicationEventPublisher.publishEvent(new ComponentRootInitializedEvent(this, root));

        CompletableFuture.runAsync(() -> {

            try {
                for (StackTraceElement stack : stackTrace) {
                    if ("main".equals(stack.getMethodName())) {

                        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                        Class<? extends Annotation> aspectClass = (Class<? extends Annotation>) Class.forName("org.aspectj.lang.annotation.Aspect", true, contextClassLoader);

                        //
                        if (aspectClass.isAnnotation()) {

                            ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
                            scanner.addIncludeFilter(new AnnotationTypeFilter(aspectClass));
                            scanner.setResourceLoader(new DefaultResourceLoader(ClassUtils.getDefaultClassLoader()));

                            //扫包组件
                            String aopDesc = findBasePackages(stack.getClassName()).stream()
                                    .map(scanner::findCandidateComponents)
                                    .flatMap(Collection::stream)
                                    .map(BeanDefinition::getBeanClassName)
                                    .collect(Collectors.joining("</br>"));
                            root.setDesc("</br> Aspectj Classes: </br> " + aopDesc);

                        }
                        break;

                    }
                }

            } catch (Exception e) {
                //ignore
                log.info("Ignores scan @Aspectj classes", e);
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
    private Set<String> findBasePackages(String springApplicationClassStr) {
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
        return Arrays.stream((String[]) mergedAnnotationAttributes.get("basePackages"))
                .map(basePackage -> {
                    String[] splitPackage = basePackage.split("\\.");
                    return splitPackage.length > 1 ? splitPackage[0] + "." + splitPackage[1] : basePackage;
                }).collect(Collectors.toSet());
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    static class AopInfo {

        private final BigDecimal startTime;

        private final String beanName;

        public AopInfo(String beanName) {
            this.beanName = beanName;
            startTime = DurationUtils.nowMillis();
        }

    }

}
