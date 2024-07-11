package com.taobao.arthas.spring.properties;

import com.taobao.arthas.spring.vo.TraceMethodProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Component
public class ArthasProperties {

    @Value("${spring.invoke.trace.delimiter}")
    private String delimiter;

    @Value("${spring.invoke.trace.methods}")
    private String methods;

    public Set<TraceMethodProperty> traceMethods() {
        if (!StringUtils.hasText(methods)) {
            return Collections.emptySet();
        } else {
            String[] invokeTracesArr = methods.split(delimiter);

            Set<TraceMethodProperty> traceMethodProperties = new HashSet<>();
            for (String invokeTrace : invokeTracesArr) {
                traceMethodProperties.add(getTraceMethodProperty(invokeTrace));
            }
            return traceMethodProperties;
        }
    }

    private TraceMethodProperty getTraceMethodProperty(String invokeTrace) {

        String[] splitProperty = invokeTrace.split("#")[1].split("\\(");

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

        return new TraceMethodProperty(
                invokeTrace.split("#")[0], splitProperty[0]
                , Arrays.stream(methodArgumentsArray).toArray(String[]::new)
        );

    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    public String getMethods() {
        return methods;
    }

    public void setMethods(String methods) {
        this.methods = methods;
    }
}
