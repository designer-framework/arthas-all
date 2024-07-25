package com.taobao.arthas.spring.configuration;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class TestMethodInvokePointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements InitializingBean {

    public TestMethodInvokePointcutAdvisor() {
        super();
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
    }

}
