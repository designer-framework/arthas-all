package com.taobao.arthas.spring.properties;

import com.taobao.arthas.core.config.Config;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.ClassMethodInfo;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@Config(prefix = "spring.profiling.invoke.trace")
public class ArthasProperties {

    private String delimiter;

    private String methods;

    public Set<ClassMethodInfo> traceMethods() {
        if (!StringUtils.hasText(methods)) {
            return Collections.emptySet();
        } else {
            String[] invokeTracesArr = methods.split(delimiter);

            Set<ClassMethodInfo> traceMethodProperties = new HashSet<>();
            for (String invokeTrace : invokeTracesArr) {
                traceMethodProperties.add(FullyQualifiedClassUtils.parserClassMethodInfo(invokeTrace));
            }
            return traceMethodProperties;
        }
    }

}
