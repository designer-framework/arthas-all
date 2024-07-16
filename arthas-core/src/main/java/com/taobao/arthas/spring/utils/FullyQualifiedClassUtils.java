package com.taobao.arthas.spring.utils;

import com.taobao.arthas.spring.vo.TraceMethodInfo;

import java.util.Arrays;

public class FullyQualifiedClassUtils {

    public static TraceMethodInfo toTraceMethodInfo(String fullyQualifiedMethodName) {
        return getTraceMethodInfo(fullyQualifiedMethodName);
    }

    /**
     * com.taobao.arthas.spring.utils.FullyQualifiedClassUtils#getTraceMethodInfo(java.lang.String)
     *
     * @param fullyQualifiedMethodName
     * @return
     */
    private static TraceMethodInfo getTraceMethodInfo(String fullyQualifiedMethodName) {
        String[] class_method_arguments = fullyQualifiedMethodName.split("#")[1].split("\\(");

        String methodArguments = class_method_arguments[1].replace(")", "");

        String[] methodArgumentsArray = methodArguments.split(",");
        if (methodArguments.isEmpty()) {
            methodArgumentsArray = new String[0];
        }

        for (int i = 0; i < methodArgumentsArray.length; i++) {
            methodArgumentsArray[i] = methodArgumentsArray[i].trim();
        }

        return new TraceMethodInfo(
                fullyQualifiedMethodName.split("#")[0], class_method_arguments[0]
                , Arrays.stream(methodArgumentsArray).toArray(String[]::new)
        );

    }

}
