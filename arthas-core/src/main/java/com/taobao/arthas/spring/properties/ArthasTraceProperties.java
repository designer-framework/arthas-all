package com.taobao.arthas.spring.properties;

import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class ArthasTraceProperties {

    private Set<String> methods;

    public Set<ClassMethodInfo> traceMethods() {

        Set<ClassMethodInfo> traceMethodProperties = new HashSet<>();
        for (String invokeTrace : methods) {
            traceMethodProperties.add(FullyQualifiedClassUtils.parserClassMethodInfo(invokeTrace));
        }
        return traceMethodProperties;

    }

}
