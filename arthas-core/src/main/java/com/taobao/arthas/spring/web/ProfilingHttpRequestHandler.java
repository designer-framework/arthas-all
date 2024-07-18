package com.taobao.arthas.spring.web;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpUtil.is100ContinueExpected;

public class ProfilingHttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final ProfilingResultVO profilingResultVO;

    public ProfilingHttpRequestHandler(ProfilingResultVO profilingResultVO) {
        this.profilingResultVO = profilingResultVO;
    }

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
