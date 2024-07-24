package com.taobao.arthas.core.utils;

import com.taobao.arthas.api.pointcut.Pointcut;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ClassUtils;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author hengyunabc 2020-05-25
 */
@Slf4j
public class InstrumentationUtils {

    public static void retransformClasses(Instrumentation inst, ClassFileTransformer transformer,
                                          Set<Class<?>> classes) {
        try {
            inst.addTransformer(transformer, true);

            for (Class<?> clazz : classes) {
                if (ClassUtils.isLambdaClass(clazz)) {
                    log.info(
                            "ignore lambda class: {}, because jdk do not support retransform lambda class: https://github.com/alibaba/arthas/issues/1512.",
                            clazz.getName());
                    continue;
                }
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                    log.error(errorMsg, e);
                }
            }
        } finally {
            inst.removeTransformer(transformer);
        }
    }

    public static void trigerRetransformClasses(Instrumentation inst, List<Pointcut> pointcuts) {

        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            //
            pointcuts.forEach(pointcut -> {

                if (pointcut.isCandidateClass(clazz.getName())) {

                    try {
                        inst.retransformClasses(clazz);
                    } catch (Throwable e) {
                        String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                        log.error(errorMsg, e);
                    }

                }

            });

        }
    }

    public static void trigerRetransformClasses(Instrumentation inst, Collection<String> classes) {
        for (Class<?> clazz : inst.getAllLoadedClasses()) {
            if (classes.contains(clazz.getName())) {
                try {
                    inst.retransformClasses(clazz);
                } catch (Throwable e) {
                    String errorMsg = "retransformClasses class error, name: " + clazz.getName();
                    log.error(errorMsg, e);
                }
            }
        }
    }

}