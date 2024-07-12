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
        String[] splitProperty = fullyQualifiedMethodName.split("#")[1].split("\\(");

        String methodArguments = splitProperty[1];

        String[] methodArgumentsArray = methodArguments.split(",");

        for (int i = 0; i < methodArgumentsArray.length; i++) {
            String trimmed = methodArgumentsArray[i].trim();
            if (i == methodArgumentsArray.length - 1) {
                methodArgumentsArray[i] = trimmed.substring(0, trimmed.length() - 1);
            } else {
                methodArgumentsArray[i] = trimmed;
            }
        }

        return new TraceMethodInfo(
                fullyQualifiedMethodName.split("#")[0], splitProperty[0]
                , Arrays.stream(methodArgumentsArray).toArray(String[]::new)
        );

    }

}
