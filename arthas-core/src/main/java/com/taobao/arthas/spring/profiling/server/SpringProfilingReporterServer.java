package com.taobao.arthas.spring.profiling.server;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.spring.constants.DisposableBeanOrdered;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
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
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

@Component
public class SpringProfilingReporterServer implements DisposableBean, Ordered {

    @Value("${server.port}")
    private int port;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    /**
     * 异步启动性能分析报表Web服务端
     */
    @Override
    public void destroy() throws Exception {
        new Thread(() -> start(port)).start();
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

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();

            System.out.println(" Profiling reporter server start up on port : http://127.0.0.1:" + port);

            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        }

    }

    @Override
    public int getOrder() {
        return DisposableBeanOrdered.START_REPORTER_SERVER;
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
            if (uri.equals("/")) {

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer(JSON.toJSONString(profilingResultVO), CharsetUtil.UTF_8)
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

            } else {

                FullHttpResponse response = new DefaultFullHttpResponse(
                        HttpVersion.HTTP_1_1,
                        HttpResponseStatus.OK,
                        Unpooled.copiedBuffer("{}", CharsetUtil.UTF_8)
                );
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");
                ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

            }

        }

    }

}
