package com.taobao.arthas.spring.web;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.listener.Reporter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.List;

@Component
public class SpringProfilingReporterServer implements ProfilingLifeCycle {

    @Value("${server.port}")
    private int port;

    @Autowired
    private List<Reporter> reporters;

    /**
     * 异步启动性能分析报表Web服务端
     */
    @Override
    public void stop() {
        new Thread(() -> start(port))
                .start();
    }

    private void start(int port) {
        try {
            //
            ServerBootstrap bootstrap = new ServerBootstrap();
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup work = new NioEventLoopGroup();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast("HttpServerCodec", new HttpServerCodec());
                            pipeline.addLast("HttpObjectAggregator", new HttpObjectAggregator(512 * 1024));
                            pipeline.addLast("HttpRequestHandler", new ProfilingHttpRequestHandler(reporters));
                        }
                    });

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();

            System.out.println(" Profiling reporter server start up on port : http://127.0.0.1:" + port);

            f.channel().closeFuture().sync();


        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        }

    }

}
