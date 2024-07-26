package com.taobao.arthas.core.profiling.hook;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteStartUpAnalysisHtmlHook implements DisposableBean {

    protected final ProfilingHtmlUtil profilingHtmlUtil;

    protected final ProfilingResultVO profilingResultVO;

    public WriteStartUpAnalysisHtmlHook(ProfilingHtmlUtil profilingHtmlUtil, ProfilingResultVO profilingResultVO) {
        this.profilingHtmlUtil = profilingHtmlUtil;
        this.profilingResultVO = profilingResultVO;
    }

    @Override
    public void destroy() throws Exception {
        CompletableFuture.allOf(
                //copy js文件
                CompletableFuture.runAsync(() -> profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.hyperappJs_)),
                CompletableFuture.runAsync(() -> profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.tailwindJs_)),
                //生成 Html文件
                CompletableFuture.runAsync(this::writeIndexHtml),
                CompletableFuture.runAsync(this::writeFlameGraph)
        ).get();
    }

    private void writeFlameGraph() {
        //
        FlameGraph fg = new FlameGraph();
        //
        fg.parse(
                //flameGraph_html源代码
                profilingHtmlUtil.resourrceToString(ProfilingHtmlUtil.flameGraph_)
                //替换占位符
                , profilingHtmlUtil.getOutputFile(ProfilingHtmlUtil.flameGraph_), profilingResultVO.getInvokeTraceMap()
        );
    }

    private void writeIndexHtml() {

        profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.startupAnalysis_, content -> {

            //替换性能分析对象占位符
            content = content.replace("/*startupVO:*/{}", JSON.toJSONString(profilingResultVO));

            //替换火焰图Path占位符
            content = content.replace("/*flameGraphUrl*/''", "'." + ProfilingHtmlUtil.flameGraph_ + "'");

            return content;

        });

    }

}
