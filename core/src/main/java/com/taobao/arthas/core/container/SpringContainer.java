package com.taobao.arthas.core.container;

import com.taobao.arthas.core.config.Configure;
import com.taobao.arthas.core.container.listener.InvokeListenerFactory;
import com.taobao.arthas.core.env.ArthasEnvironment;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InitDestroyAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 生命周期实例容器，由容器统一控制
 *
 * @author linyimin
 **/
public class SpringContainer {

    //private static final Logger startupLogger = LogFactory.getStartupLogger();

    private static final DefaultListableBeanFactory springContainer = new DefaultListableBeanFactory();

    private static final AtomicBoolean springStarted = new AtomicBoolean();

    private static final AtomicBoolean springStopped = new AtomicBoolean();

    private static long startTime = 0L;

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

            springContainer.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
            springContainer.addBeanPostProcessor(new InitDestroyAnnotationBeanPostProcessor());
            //
            List<InvokeListenerFactory> invokeListeners = SpringFactoriesLoader.loadFactories(InvokeListenerFactory.class, InvokeListenerFactory.class.getClassLoader());

            //注入容器中
            for (InvokeListenerFactory invokeListener : invokeListeners) {

                String beanName = invokeListener.getClass().getSimpleName();
                BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(invokeListener.getClass());
                definition.setScope(BeanDefinition.SCOPE_SINGLETON);
                springContainer.registerBeanDefinition(beanName, definition.getBeanDefinition());

                String[] beanDefinitionNames = springContainer.getBeanDefinitionNames();
                for (String beanDefinitionName : beanDefinitionNames) {
                    springContainer.getBean(beanDefinitionName);
                }

            }

        }

    }

    /**
     * 停止容器
     */
    public static void stop() {

        //销毁容器
        if (!springStopped.compareAndSet(false, true)) {
            springContainer.destroySingletons();
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
