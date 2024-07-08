package com.taobao.arthas.core.container;

import com.taobao.arthas.core.config.Configure;
import com.taobao.arthas.core.container.configuration.InvokeBeanDefinitionRegistryPostProcessor;
import com.taobao.arthas.core.container.handler.InvokeAdviceHandler;
import com.taobao.arthas.core.env.ArthasEnvironment;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生命周期实例容器，由容器统一控制
 *
 * @author linyimin
 **/
public class SpringContainer {

    //private static final Logger startupLogger = LogFactory.getStartupLogger();

    private static final AnnotationConfigApplicationContext springContainer = new AnnotationConfigApplicationContext();

    private static final AtomicBoolean springStarted = new AtomicBoolean();

    private static final AtomicBoolean springStopped = new AtomicBoolean();

    private static long startTime = 0L;

    public static void main(String[] args) {
        InvokeAdviceHandler bean = springContainer.getBean(InvokeAdviceHandler.class);
    }

    /**
     * 启动服务容器
     */
    public static void run(ArthasEnvironment arthasEnvironment, Configure configure) {
        start();
        startTime = System.currentTimeMillis();
    }

    public static void start() {

        //初始化容器
        if (!springStarted.compareAndSet(false, true)) {

            springContainer.addBeanFactoryPostProcessor(new InvokeBeanDefinitionRegistryPostProcessor());
            springContainer.scan("com.designer.turbo.tests.container");
            springContainer.refresh();

        }

    }

    /**
     * 停止容器
     */
    public static void stop() {

        //销毁容器
        if (!springStopped.compareAndSet(false, true)) {
            springContainer.close();
            return;
        }

        //启动耗时
        double startupDuration = (System.currentTimeMillis() - startTime) / 1000D;

        System.out.println("启动耗时: " + startupDuration);

    }

    public static <T> T getBean(Class<T> componentType) {
        return springContainer.getBean(componentType);
    }

    public static <T> Collection<T> getBeans(Class<T> componentType) {
        return springContainer.getBeansOfType(componentType).values();
    }

}
