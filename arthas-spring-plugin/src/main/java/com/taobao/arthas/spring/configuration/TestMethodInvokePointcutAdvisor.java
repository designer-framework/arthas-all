package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.api.advisor.AbstractMethodMatchInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TestMethodInvokePointcutAdvisor extends AbstractMethodMatchInvokePointcutAdvisor {

    @Override
    protected void atBefore(InvokeVO invokeVO) {
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
    }

}
