package com.taobao.arthas.core.properties;

import com.taobao.arthas.core.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class ArthasMethodTraceProperties {

    private Set<String> methods;

    public Set<ClassMethodInfo> traceMethods() {

        Set<ClassMethodInfo> traceMethodProperties = new HashSet<>();
        for (String invokeTrace : methods) {
            traceMethodProperties.add(FullyQualifiedClassUtils.parserClassMethodInfo(invokeTrace));
        }
        return traceMethodProperties;

    }

}
