package com.taobao.arthas.plugin.core.web;

import com.taobao.arthas.plugin.core.annotation.WebController;
import com.taobao.arthas.plugin.core.annotation.WebMapping;
import com.taobao.arthas.plugin.core.utils.ProfilingHtmlUtil;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;

@WebController
public class AgentWebApi {

    @Autowired
    private SpringAgentStatisticsVO springAgentStatisticsVO;

    @Autowired
    private ProfilingHtmlUtil profilingHtmlUtil;

    /**
     * 报表首页
     *
     * @param uri
     * @return
     */
    @WebMapping({"/", ProfilingHtmlUtil.startupAnalysis_})
    public String startupAnalysis(String uri) {
        return profilingHtmlUtil.readOutputResourrceToString(ProfilingHtmlUtil.startupAnalysis_);
    }

    /**
     * 火焰图
     *
     * @param uri
     * @return
     */
    @WebMapping({ProfilingHtmlUtil.flameGraph_})
    public String flameGraph(String uri) {
        return profilingHtmlUtil.readOutputResourrceToString(uri);
    }

    /**
     * 静态资源
     *
     * @param uri
     * @return
     */
    @WebMapping({"/*.js"})
    public String statics(String uri) {
        return profilingHtmlUtil.resourrceToString(uri);
    }

    /**
     * 报表统计数据
     *
     * @param uri
     * @return
     */
    @WebMapping({"/analysis/json"})
    public SpringAgentStatisticsVO springAgentStatisticsVO(String uri) {
        return springAgentStatisticsVO;
    }

}
