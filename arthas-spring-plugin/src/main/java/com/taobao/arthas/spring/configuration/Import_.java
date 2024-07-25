package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.core.annotation.EnabledMethodInvokeWatch;
import com.taobao.arthas.core.annotation.MethodInvokeWatch;
import org.springframework.context.annotation.Configuration;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-25 23:42
 */
@EnabledMethodInvokeWatch({
        @MethodInvokeWatch(value = "com.lcsc.profiling.web.configuration.Config#stringxxx()")
})
@Configuration
public class Import_ {
}
