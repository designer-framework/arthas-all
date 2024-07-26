package com.taobao.arthas.plugin.core.profiling.hook;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteStartUpAnalysisHtmlHook implements DisposableBean {

    protected final ProfilingHtmlUtil profilingHtmlUtil;

    protected final SpringAgentStatisticsVO springAgentStatisticsVO;

    public WriteStartUpAnalysisHtmlHook(ProfilingHtmlUtil profilingHtmlUtil, SpringAgentStatisticsVO springAgentStatisticsVO) {
        this.profilingHtmlUtil = profilingHtmlUtil;
        this.springAgentStatisticsVO = springAgentStatisticsVO;
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
                , profilingHtmlUtil.getOutputFile(ProfilingHtmlUtil.flameGraph_), springAgentStatisticsVO.invokeStackTraceMap()
        );
    }

    private void writeIndexHtml() {

        profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.startupAnalysis_, content -> {

            //替换性能分析对象占位符
            content = content.replace("/*startupVO:*/{}", JSON.toJSONString(springAgentStatisticsVO));

            //替换火焰图Path占位符
            content = content.replace("/*flameGraphUrl*/''", "'." + ProfilingHtmlUtil.flameGraph_ + "'");

            return content;

        });

    }

}
