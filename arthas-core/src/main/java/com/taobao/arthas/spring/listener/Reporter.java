package com.taobao.arthas.spring.listener;

import com.taobao.arthas.spring.vo.ReportVO;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 20:39
 */
public interface Reporter {

    ReportVO getReportVO();

    default void release() {
    }

}
