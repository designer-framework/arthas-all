package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.events.ComponentChildInitializedEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import java.lang.annotation.Annotation;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
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

    @Override
    public void start() {
        super.start();

        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        CompletableFuture.runAsync(() -> {

            try {
                Class<? extends Annotation> aspectClass = (Class<? extends Annotation>) Class.forName("org.aspectj.lang.annotation.Aspect", true, contextClassLoader);

                if (aspectClass.isAnnotation()) {

                    ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
                    scanner.addIncludeFilter(new AnnotationTypeFilter(aspectClass));
                    scanner.setResourceLoader(new DefaultResourceLoader(contextClassLoader));
                    Set<BeanDefinition> candidateComponents = scanner.findCandidateComponents("com.lcsc");

                    System.out.println(candidateComponents.size());
                    List<InitializedComponent.Children> children = candidateComponents.stream()
                            .map(beanDefinition ->
                                    InitializedComponent.child(SpringComponentEnum.CLASS_PATH_SCANNING, beanDefinition.getBeanClassName(), BigDecimal.ZERO)
                            ).collect(Collectors.toList());

                    applicationEventPublisher.publishEvent(new ComponentChildInitializedEvent(this, children));

                }

            } catch (Exception e) {
                //ignore
                log.info("Ignore Aspectj scanner", e);
            }
        });

    }

}
