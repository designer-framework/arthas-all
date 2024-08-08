package com.taobao.arthas.plugin.core.profiling.hook.server;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-09 00:29
 */
public interface Handler {

    Handler INDEX = new MethodHandler(null, null) {
        @Override
        public Object handler(Object param) throws Exception {
            return "Spring-Agent-Web-Server";
        }
    };

    Object handler(Object param) throws Exception;

}
