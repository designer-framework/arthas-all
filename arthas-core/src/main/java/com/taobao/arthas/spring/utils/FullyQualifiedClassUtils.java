package com.taobao.arthas.spring.utils;

import com.taobao.arthas.spring.vo.ClassMethodInfo;

import java.util.Arrays;

public class FullyQualifiedClassUtils {

    public static ClassMethodInfo parserClassMethodInfo(String fullyQualifiedMethodName) {
        return getClassMethodInfo(fullyQualifiedMethodName);
    }

    /**
     * @param fullyQualifiedMethodName
     * @return
     * @see com.taobao.arthas.spring.utils.FullyQualifiedClassUtils#getClassMethodInfo(java.lang.String)
     */
    private static ClassMethodInfo getClassMethodInfo(String fullyQualifiedMethodName) {
        //类命
        String className = fullyQualifiedMethodName.split("#")[0].trim();
        //方法名
        String[] method_arguments = fullyQualifiedMethodName.split("#")[1].split("\\(");
        String methodName = method_arguments[0].trim();
        //入参类型
        String methodArgumentsStr = method_arguments[1].replace(")", "");
        String[] methodArguments = methodArgumentsStr.split(",");
        if (methodArgumentsStr.isEmpty()) {
            methodArguments = new String[0];
        }
        for (int i = 0; i < methodArguments.length; i++) {
            methodArguments[i] = methodArguments[i].trim();
        }

        return new ClassMethodInfo(
                fullyQualifiedMethodName
                , className, methodName, Arrays.stream(methodArguments).toArray(String[]::new)
        );

    }

}
