package com.taobao.arthas.spring.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
@Component
public class ArthasThreadProfilingProperties {

    /**
     * 被采样线程名
     */
    @Value("#{'${spring.profiling.thread.names:}'.split(',')}")
    private Set<String> threadNames = new HashSet<>(Collections.singletonList("main"));

    /**
     * 采样间隔
     */
    @Value("${spring.profiling.thread.interval}")
    private long interval = 1;

}
