package com.taobao.arthas.plugin.core.profiling.hook;

import com.taobao.arthas.core.constants.LifeCycleStopHookOrdered;
import com.taobao.arthas.plugin.core.properties.ArthasServerProperties;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.Ordered;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

@Slf4j
public class StartReporterServerHook implements DisposableBean, Ordered {

    private final ArthasServerProperties arthasServerProperties;

    private final ProfilingHtmlUtil profilingHtmlUtil;

    public StartReporterServerHook(ArthasServerProperties arthasServerProperties, ProfilingHtmlUtil profilingHtmlUtil) {
        this.arthasServerProperties = arthasServerProperties;
        this.profilingHtmlUtil = profilingHtmlUtil;
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

            log.error("点击查看性能分析报告: http://127.0.0.1:{}", port);

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        }

    }

    @Override
    public int getOrder() {
        return LifeCycleStopHookOrdered.START_REPORTER_SERVER;
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

            //报表首页
            if ("/".equals(uri) || ProfilingHtmlUtil.startupAnalysis_.equals(uri)) {

                response(ctx, profilingHtmlUtil.readOutputResourrceToString(ProfilingHtmlUtil.startupAnalysis_));

                // 火焰图
            } else if (ProfilingHtmlUtil.flameGraph_.equals(uri)) {

                response(ctx, profilingHtmlUtil.readOutputResourrceToString(uri));

                // 静态资源
            } else if (uri.lastIndexOf(".js") > -1) {

                response(ctx, profilingHtmlUtil.resourrceToString(uri));

            }

        }

        private void response(ChannelHandlerContext ctx, String content) {

            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.OK
                    , Unpooled.copiedBuffer(content, CharsetUtil.UTF_8)
            );
            response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

        }

    }

}
