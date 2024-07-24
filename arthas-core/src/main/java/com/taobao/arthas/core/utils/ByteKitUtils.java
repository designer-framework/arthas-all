package com.taobao.arthas.core.utils;

import com.alibaba.deps.org.objectweb.asm.Type;
import org.apache.commons.lang3.StringUtils;

public class ByteKitUtils {

    /**
     * 翻译类名称<br/>
     * 将 java/lang/String 的名称翻译成 java.lang.String
     *
     * @param className 类名称 java/lang/String
     * @return 翻译后名称 java.lang.String
     */
    public static String normalizeClassName(String className) {
        return StringUtils.replace(className, "/", ".");
    }

    /**
     * 方法签名解析成易于阅读的字段
     *
     * @param methodDesc
     * @return
     */
    public static String[] getMethodArgumentTypes(String methodDesc) {
        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        //方法入参对应的JAVA类型
        String[] javaArgumentTypes = new String[argumentTypes.length];

        for (int i = 0; i < argumentTypes.length; i++) {
            javaArgumentTypes[i] = argumentTypes[i].getClassName();
        }

        return javaArgumentTypes;
    }

}
