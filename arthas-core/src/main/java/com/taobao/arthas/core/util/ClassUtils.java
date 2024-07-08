package com.taobao.arthas.core.util;

import java.security.CodeSource;

/**
 * @author hengyunabc 2018-10-18
 */
public class ClassUtils {

    public static String getCodeSource(CodeSource cs) {
        if (null == cs || null == cs.getLocation() || null == cs.getLocation().getFile()) {
            return com.taobao.arthas.core.util.Constants.EMPTY_STRING;
        }

        return cs.getLocation().getFile();
    }

    public static boolean isLambdaClass(Class<?> clazz) {
        return clazz.getName().contains("$$Lambda$");
    }

}
