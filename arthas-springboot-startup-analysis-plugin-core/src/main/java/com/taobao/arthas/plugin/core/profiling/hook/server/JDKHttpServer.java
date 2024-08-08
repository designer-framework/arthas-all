/*
 * Copyright The async-profiler authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.taobao.arthas.plugin.core.profiling.hook.server;

import com.alibaba.fastjson.JSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class JDKHttpServer extends Thread implements Executor, HttpHandler {

    private final HttpServer server;

    private final AtomicInteger threadNum = new AtomicInteger();

    private final HandlerMapping handlerMapping;

    public JDKHttpServer(int port, HandlerMapping handlerMapping) throws IOException {
        super("Spring-Agent-Web-Server");
        setDaemon(true);

        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", this);
        server.setExecutor(this);

        this.handlerMapping = handlerMapping;
    }

    @Override
    public void run() {
        server.start();
    }

    @Override
    public void execute(Runnable requestRunnable) {
        Thread t = new Thread(requestRunnable, "Agent Request #" + threadNum.incrementAndGet());
        t.setDaemon(false);
        t.start();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            //
            URI requestURI = exchange.getRequestURI();
            Handler handler = handlerMapping.getHandler(requestURI.getPath(), Collections.emptyMap());

            if (handler != null) {

                //
                Object response = handler.handler(exchange);
                if (response instanceof String) {
                    sendResponse(exchange, 200, (String) response);
                } else {
                    sendResponse(exchange, 200, JSON.toJSONString(response));
                }

                //404
            } else {

                sendResponse(exchange, 404, "404 Not Fount");

            }

        } catch (IllegalArgumentException e) {
            sendResponse(exchange, 400, e.getMessage());
        } catch (Exception e) {
            sendResponse(exchange, 500, e.getMessage());
        } finally {
            exchange.close();
        }
    }

    public Map<String, String> getParam(URI requestURI) {
        if (StringUtils.isEmpty(requestURI.getQuery())) {
            return Collections.emptyMap();
        }

        String[] params = StringUtils.split(requestURI.getQuery(), "&");
        if (params.length == 0) {
            return Collections.emptyMap();
        }

        return Arrays.stream(params)
                .map(param -> StringUtils.split("="))
                .collect(Collectors.toMap(s -> StringUtils.stripToNull(s[0]), s -> StringUtils.stripToNull(s[1])));
    }

    private void sendResponse(HttpExchange exchange, int code, String body) throws IOException {
        String contentType = body.startsWith("<!DOCTYPE html>") ? "text/html; charset=utf-8" : "text/plain";
        exchange.getResponseHeaders().add("Content-Type", contentType);

        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(code, bodyBytes.length);
        exchange.getResponseBody().write(bodyBytes);
    }

}
