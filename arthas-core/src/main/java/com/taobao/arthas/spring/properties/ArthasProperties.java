package com.taobao.arthas.spring.properties;

import com.taobao.arthas.core.config.Config;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Config(prefix = "spring.profiling.invoke.trace")
public class ArthasProperties {

    private String delimiter;

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
