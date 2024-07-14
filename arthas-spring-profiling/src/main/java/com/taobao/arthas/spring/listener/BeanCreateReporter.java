package com.taobao.arthas.spring.listener;

import com.taobao.arthas.spring.vo.ReportVO;

public interface BeanCreateReporter<T> extends Reporter {

    @Override
    ReportVO getReportVO();

}
