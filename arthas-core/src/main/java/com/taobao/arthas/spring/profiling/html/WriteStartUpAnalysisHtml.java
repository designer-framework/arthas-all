package com.taobao.arthas.spring.profiling.html;

import com.alibaba.fastjson.JSON;
import com.taobao.arthas.spring.utils.AgentHomeUtil;
import com.taobao.arthas.spring.utils.ProfilingHtmlUtil;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WriteStartUpAnalysisHtml implements DisposableBean {

    @Autowired
    private ProfilingHtmlUtil profilingHtmlUtil;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    @Override
    public void destroy() throws Exception {

        profilingHtmlUtil.writeHtml(ProfilingHtmlUtil.hyperappJs_);

        profilingHtmlUtil.writeHtml(ProfilingHtmlUtil.tailwindJs_);

        writeFlameGraph();

        writeStartUpAnalysis();

    }

    private void writeStartUpAnalysis() {

        profilingHtmlUtil.writeHtml(ProfilingHtmlUtil.startupAnalysis_, content -> {

            content = content.replace("/*startupVO:*/{}", JSON.toJSONString(profilingResultVO));

            content = content.replace("/*flameGraphUrl*/''", "'./" + ProfilingHtmlUtil.flameGraph_ + "'");

            return content;

        });

    }

    private void writeFlameGraph() {

        //
        FlameGraph fg = new FlameGraph();
        //
        profilingHtmlUtil.writeHtml(ProfilingHtmlUtil.flameGraph_, flameGraphSource -> {

            fg.parse(flameGraphSource, AgentHomeUtil.getOutputPath(ProfilingHtmlUtil.flameGraph_), profilingResultVO.getInvokeTraceMap());

        });

    }

}
