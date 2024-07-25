package com.taobao.arthas.core.profiling.html;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.core.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class WriteStartUpAnalysisHtml implements DisposableBean {

    @Autowired
    private ProfilingHtmlUtil profilingHtmlUtil;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    @Override
    public void destroy() throws Exception {
        CompletableFuture.allOf(
                //copy js等静态文件
                CompletableFuture.runAsync(this::writeStaticResource),
                //生成Html文件
                CompletableFuture.runAsync(this::writeStartUpAnalysis),
                CompletableFuture.runAsync(this::writeFlameGraph)
        ).get();
    }

    private void writeStaticResource() {
        profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.hyperappJs_, ProfilingHtmlUtil.tailwindJs_);
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

    private void writeStartUpAnalysis() {

        profilingHtmlUtil.copyToOutputPath(ProfilingHtmlUtil.startupAnalysis_, content -> {

            //替换性能分析对象占位符
            content = content.replace("/*startupVO:*/{}", JSON.toJSONString(profilingResultVO));

            //替换火焰图Path占位符
            content = content.replace("/*flameGraphUrl*/''", "'." + ProfilingHtmlUtil.flameGraph_ + "'");

            return content;

        });

    }

}
