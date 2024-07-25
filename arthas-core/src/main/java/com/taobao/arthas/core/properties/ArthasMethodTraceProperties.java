package com.taobao.arthas.core.properties;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    @NoArgsConstructor
    @AllArgsConstructor
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
            return ClassMethodInfo.create(method);
        }

        @Override
        public String toString() {
            return "{\"method\": \"" + method + '\"' +
                    ", \"canRetransform\": \"" + canRetransform + "\"}";
        }

    }

}
