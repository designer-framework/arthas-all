package com.taobao.arthas.spring.properties;

import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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

    public Set<TraceMethodInfo> traceMethods() {
        if (!StringUtils.hasText(methods)) {
            return Collections.emptySet();
        } else {
            String[] invokeTracesArr = methods.split(delimiter);

            Set<TraceMethodInfo> traceMethodProperties = new HashSet<>();
            for (String invokeTrace : invokeTracesArr) {
                traceMethodProperties.add(FullyQualifiedClassUtils.toTraceMethodInfo(invokeTrace));
            }
            return traceMethodProperties;
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
