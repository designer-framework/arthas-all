package com.taobao.arthas.plugin.core.web;

import com.sun.net.httpserver.HttpExchange;
import com.taobao.arthas.plugin.core.annotation.WebController;
import com.taobao.arthas.plugin.core.annotation.WebMapping;
import com.taobao.arthas.plugin.core.enums.StatisticsEnum;
import com.taobao.arthas.plugin.core.profiling.statistics.StatisticsAggregation;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

@WebController
public class AgentWebApi {

    @Autowired
    private StatisticsAggregation statisticsAggregation;

    @Autowired
    private ProfilingHtmlUtil profilingHtmlUtil;

    /**
     * 报表首页
     *
     * @param exchange
     * @return
     */
    @WebMapping({"/", ProfilingHtmlUtil.startupAnalysis_})
    public String startupAnalysis(HttpExchange exchange) {
        return profilingHtmlUtil.readOutputResourrceToString(ProfilingHtmlUtil.startupAnalysis_);
    }

    /**
     * 火焰图
     *
     * @param exchange
     * @return
     */
    @WebMapping({ProfilingHtmlUtil.flameGraph_})
    public String flameGraph(HttpExchange exchange) {
        return profilingHtmlUtil.readOutputResourrceToString(exchange.getRequestURI().getPath());
    }

    /**
     * 静态资源
     *
     * @param exchange
     * @return
     */
    @WebMapping({"/*.js"})
    public String statics(HttpExchange exchange) {
        return profilingHtmlUtil.resourrceToString(exchange.getRequestURI().getPath());
    }

    /**
     * 报表统计数据
     *
     * @param exchange
     * @return
     */
    @WebMapping(value = {"/analysis/json"}, contentType = "application/json; charset=utf-8")
    public Object springAgentStatisticsVO(HttpExchange exchange, AnalysisJsonVO analysisJsonVO) {
        //
        if (analysisJsonVO.type == null) {

            return Collections.emptyMap();

            //
        } else {

            Object statistics = statisticsAggregation.statistics(analysisJsonVO.type);
            if (statistics != null) {
                return statistics;
            } else {
                return Collections.emptyMap();
            }

        }

    }

    @Data
    static class AnalysisJsonVO {
        private StatisticsEnum type;
    }

}
