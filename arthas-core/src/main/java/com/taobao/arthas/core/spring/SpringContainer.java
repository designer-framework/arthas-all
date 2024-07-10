package com.taobao.arthas.core.spring;

import com.taobao.arthas.core.config.Configure;
import com.taobao.arthas.core.env.ArthasEnvironment;
import com.taobao.arthas.core.spring.configuration.ArthasExtensionSpringPostProcessor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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

    static {
        springContainer.setClassLoader(SpringContainer.class.getClassLoader());
    }

    /**
     * 启动服务容器
     */
    public static void run(ArthasEnvironment arthasEnvironment, Configure configure) {
        start(arthasEnvironment, configure);
        startTime = System.currentTimeMillis();
    }

    /**
     * 环境变量及相关配置可以考虑注入容器
     *
     * @param arthasEnvironment
     * @param configure
     */
    public static void start(ArthasEnvironment arthasEnvironment, Configure configure) {

        //初始化容器
        if (springStarted.compareAndSet(false, true)) {
            springContainer.addBeanFactoryPostProcessor(new ArthasExtensionSpringPostProcessor());
            springContainer.scan(configure.getScanPackages().split(","));
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

}
