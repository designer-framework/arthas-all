package com.taobao.arthas.spring.properties;

import lombok.Data;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-10 22:25
 */
@Data
public class ArthasThreadTraceProperties {

    /**
     * 被采样线程名
     */
    private Set<String> names = new HashSet<>(Collections.singletonList("main"));

    /**
     * 采样间隔
     */
    private long interval = 1;

}
