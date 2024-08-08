package com.taobao.arthas.plugin.core.profiling.hook.server;

import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
public interface HandlerMapping {

    /**
     * 匹配API接口
     *
     * @param uri
     * @param param
     * @return
     */
    Handler getHandler(String uri, Map<String, Object> param);

}

