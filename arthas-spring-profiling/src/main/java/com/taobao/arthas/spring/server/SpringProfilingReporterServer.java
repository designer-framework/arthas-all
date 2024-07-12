package com.taobao.arthas.spring.server;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.events.BeanCreatedEvent;
import com.taobao.arthas.spring.report.BeanCreateReporter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
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
    private BeanCreateReporter beanCreateReporter;

    /**
     * 异步启动性能分析报表Web服务端
     */
    @Override
    public void stop() {
        new Thread(() -> start(port))
                .start();
    }

    public void start(int port) {

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup work = new NioEventLoopGroup();
            bootstrap.group(boss, work)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new HttpServerInitializer());

            ChannelFuture f = bootstrap.bind(new InetSocketAddress(port)).sync();

            List<BeanCreatedEvent> beanCreatedEvents = beanCreateReporter.getBeanCreatedEvents();
            System.out.println(" Profiling reporter server start up on port : http://127.0.0.1:" + port);

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
