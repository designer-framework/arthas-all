package com.taobao.arthas.core.properties;

import com.taobao.arthas.core.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.core.vo.ClassMethodInfo;
import lombok.Data;

import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class ArthasMethodTraceProperties {

    private Set<ClassMethodDesc> methods;

    @Data
    public static class ClassMethodDesc {
        
        /**
         * 全限定方法名
         * fullyQualifiedMethodName
         */
        private String method;

        /**
         * 是否允许重新加载已被装载的类
         */
        private Boolean canRetransform = Boolean.FALSE;

        public ClassMethodInfo getMethodInfo() {
            return FullyQualifiedClassUtils.parserClassMethodInfo(method);
        }

        @Override
        public String toString() {
            return "{" +
                    "method: \"" + method + '\"' +
                    ", canRetransform: \"" + canRetransform + "\"" +
                    "}";
        }

    }

}
