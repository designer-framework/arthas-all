package com.taobao.arthas.plugin.core.profiling.hook.server;

import com.sun.net.httpserver.HttpExchange;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
public interface Handler {

    void handler(HttpExchange exchange) throws Exception;

}
