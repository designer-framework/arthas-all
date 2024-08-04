package com.taobao.arthas.plugin.core.profiling.hook;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.core.flamegraph.FlameGraph;
import com.taobao.arthas.plugin.core.profiling.statistics.StatisticsAggregation;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.CompletableFuture;

@Slf4j
public class WriteStartUpAnalysisHtmlHook implements DisposableBean {

    protected final ProfilingHtmlUtil profilingHtmlUtil;

    protected final StatisticsAggregation statisticsAggregation;

    protected final FlameGraph flameGraph;

    public WriteStartUpAnalysisHtmlHook(
            ProfilingHtmlUtil profilingHtmlUtil
            , StatisticsAggregation statisticsAggregation
            , FlameGraph flameGraph
    ) {
        this.profilingHtmlUtil = profilingHtmlUtil;
        this.statisticsAggregation = statisticsAggregation;
        this.flameGraph = flameGraph;
    }

    @Override
    public void destroy() throws Exception {
        CompletableFuture.allOf(
                //copy js文件
                CompletableFuture.runAsync(() -> profilingHtmlUtil.copyFileToOutputPath(ProfilingHtmlUtil.hyperappJs_)),
                CompletableFuture.runAsync(() -> profilingHtmlUtil.copyFileToOutputPath(ProfilingHtmlUtil.tailwindJs_)),
                //导出首页
                CompletableFuture.runAsync(this::writeIndexHtml),
                //导出火焰图
                CompletableFuture.runAsync(() -> {
                    flameGraph.write(profilingHtmlUtil.getOutputFile(ProfilingHtmlUtil.flameGraph_));
                })
        ).get();
    }

    private void writeIndexHtml() {

        profilingHtmlUtil.copyFileToOutputPath(ProfilingHtmlUtil.startupAnalysis_, content -> {

            //替换性能分析对象占位符0
            content = content.replace("/*startupVO:*/{}", JSON.toJSONString(statisticsAggregation.statisticsAggregation()));

            //替换火焰图Path占位符
            content = content.replace("/*flameGraphUrl*/''", "'." + ProfilingHtmlUtil.flameGraph_ + "'");

            return content;

        });

    }

}
