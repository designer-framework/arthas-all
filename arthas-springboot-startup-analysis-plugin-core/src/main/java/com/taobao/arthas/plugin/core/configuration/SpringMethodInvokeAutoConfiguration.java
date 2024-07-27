package com.taobao.arthas.plugin.core.configuration;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-26 23:04
 */
@Configuration(proxyBeanMethods = false)
@EnabledMethodInvokeWatch({
        //spring-boot3.2.0+= 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.launch.LaunchedClassloader#loadClass(java.lang.String, boolean)"),
        //spring-boot3.2.0- 类加载器
        @MethodInvokeWatch("org.springframework.boot.loader.LaunchedURLClassLoader#loadClass(java.lang.String, boolean)")
})
public class SpringMethodInvokeAutoConfiguration {
}
