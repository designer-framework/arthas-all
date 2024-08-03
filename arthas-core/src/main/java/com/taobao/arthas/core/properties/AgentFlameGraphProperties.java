package com.taobao.arthas.core.properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@Slf4j
public class AgentFlameGraphProperties implements InitializingBean {

    private boolean enabled = false;

    private boolean highPrecision;

    /**
     * 被采样线程名
     */
    private Set<String> names = new HashSet<>(Collections.singletonList("main"));

    /**
     * 采样间隔, 值越小精度越高
     */
    private long interval = 1;

    /**
     * 采样噪点
     */
    private String[] skipTrace = new String[]{"java.arthas", "com.taobao.arthas"};

    @Override
    public void afterPropertiesSet() throws Exception {
        if (highPrecision) {
            if (interval != 1) {
                log.warn("Flame map with high-precision sampling mode enabled, method call statistics turned off. & interval: {} -> 1", interval);
                interval = 1;
            } else {
                log.warn("Flame map with high-precision sampling mode enabled, method call statistics turned off.");
            }
        }
    }

}
