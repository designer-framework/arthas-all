package com.taobao.arthas.spring.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

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

    public Set<String> traceMethods() {
        if (!StringUtils.hasText(methods)) {
            return Collections.emptySet();
        } else {
            String[] invokeTracesArr = methods.split(delimiter);
            return Arrays.stream(invokeTracesArr).map(String::trim).collect(Collectors.toSet());
        }
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
