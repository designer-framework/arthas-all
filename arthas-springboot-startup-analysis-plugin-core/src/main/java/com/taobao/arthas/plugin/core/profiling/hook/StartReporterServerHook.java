package com.taobao.arthas.plugin.core.profiling.hook;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.plugin.core.annotation.WebController;
import com.taobao.arthas.plugin.core.annotation.WebMapping;
import com.taobao.arthas.plugin.core.properties.ArthasServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

@Slf4j
public class StartReporterServerHook implements SmartInitializingSingleton, ApplicationContextAware, DisposableBean, Ordered {

    private static final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final ArthasServerProperties arthasServerProperties;

    private final Map<String, Handler> handlerMap = new HashMap<>();

    @Setter
    private ApplicationContext applicationContext;

    public StartReporterServerHook(ArthasServerProperties arthasServerProperties) {
        this.arthasServerProperties = arthasServerProperties;
    }

    /**
     * 异步启动性能分析报表Web服务端
     */
    @Override
    public void destroy() throws Exception {
        new Thread(() -> start(arthasServerProperties.getPort()))
                .start();
    }

    private void start(int port) {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();
        //
        ServerBootstrap bootstrap = new ServerBootstrap();
        try {
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("HttpServerCodec", new HttpServerCodec());
                            pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(512 * 1024));
                            pipeline.addLast("HttpRequestHandler", new ProfilingHttpRequestHandler());
                        }

                    });
            //io.netty.handler.codec.DefaultHeaders.ValueValidator
            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();

            log.error("Click to view the performance analysis report: http://127.0.0.1:{}", port);

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        }

    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.START_REPORTER_SERVER;
    }

    @Override
    public void afterSingletonsInstantiated() {

        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(WebController.class);
        beansWithAnnotation.forEach((name, bean) -> {

            ReflectionUtils.doWithMethods(bean.getClass(), method -> {

                WebMapping webMapping = method.getAnnotation(WebMapping.class);

                if (webMapping != null) {

                    for (String mapping : webMapping.value()) {
                        handlerMap.put(mapping, new Handler(bean, method));
                    }

                }

            });

        });

    }

    @Data
    static class Handler {

        private Object bean;

        private Method method;

        public Handler(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

    }

    class ProfilingHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest req) throws Exception {
            if (is100ContinueExpected(req)) {
                ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
            }

            // 获取请求的uri
            String uri = req.uri();

            Handler handler = handlerMap.get(uri);
            if (handler == null) {

                for (Map.Entry<String, Handler> handlerEntry : handlerMap.entrySet()) {

                    if (antPathMatcher.match(handlerEntry.getKey(), uri)) {

                        //bestMatch
                        handler = handlerEntry.getValue();
                        response(ctx, handler.method.invoke(handler.bean, uri));
                        break;

                    }

                }

                response(ctx, null);

            } else {

                response(ctx, handler.method.invoke(handler.bean, uri));

            }


        }

        private void response(ChannelHandlerContext ctx, Object content) {
            if (content instanceof String) {
                response(ctx, (String) content, HttpHeaderValues.TEXT_HTML.toString());
            } else {
                response(ctx, JSON.toJSONString(content), HttpHeaderValues.APPLICATION_JSON.toString());
            }
        }

        private void response(ChannelHandlerContext ctx, String content, String contentType) {
            ByteBuf contentBuf = content == null ?
                    Unpooled.EMPTY_BUFFER : Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);

            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, contentBuf);
            response.headers()
                    .set(HttpHeaderNames.CONTENT_TYPE, contentType)
                    .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                    .set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true)
            ;
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

    }

}
