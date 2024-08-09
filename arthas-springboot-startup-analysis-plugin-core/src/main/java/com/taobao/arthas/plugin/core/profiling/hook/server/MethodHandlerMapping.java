package com.taobao.arthas.plugin.core.profiling.hook.server;

import com.taobao.arthas.plugin.core.annotation.WebController;
import com.taobao.arthas.plugin.core.annotation.WebMapping;
import lombok.Setter;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:39
 */
@Setter
public class MethodHandlerMapping implements HandlerMapping, ApplicationContextAware, SmartInitializingSingleton {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final Map<String, MethodHandler> handlerMap = new HashMap<>();

    private ApplicationContext applicationContext;

    @Override
    public Handler getHandler(String uri) {
        //
        MethodHandler handler = handlerMap.get(uri);
        //
        if (handler != null) {

            return handler;

        } else {

            for (Map.Entry<String, MethodHandler> handlerEntry : handlerMap.entrySet()) {

                if (antPathMatcher.match(handlerEntry.getKey(), uri)) {

                    //bestMatch
                    handler = handlerEntry.getValue();

                    return handler;

                }

            }

        }
        return null;
    }

    @Override
    public void afterSingletonsInstantiated() {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(WebController.class);
        //
        beansWithAnnotation.forEach((name, bean) -> {

            ReflectionUtils.doWithMethods(bean.getClass(), method -> {

                WebMapping webMapping = method.getAnnotation(WebMapping.class);

                if (webMapping != null) {

                    for (String mapping : webMapping.value()) {
                        handlerMap.put(mapping, new MethodHandler(bean, method, webMapping.contentType()));
                    }

                }

            });

        });
    }

}
