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
        @MethodInvokeWatch(value = "com.taobao.arthas.spring.configuration.TestMethodInvokePointcutAdvisor#atBefore(com.taobao.arthas.api.vo.InvokeVO)")
})
@Configuration
public class Import_ {
}
