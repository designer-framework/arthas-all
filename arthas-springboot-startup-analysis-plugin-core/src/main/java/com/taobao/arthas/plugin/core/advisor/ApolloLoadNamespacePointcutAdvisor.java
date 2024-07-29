package com.taobao.arthas.plugin.core.advisor;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.events.LoadApolloNamespaceEvent;
import com.taobao.arthas.plugin.core.vo.InitializedComponent;
import lombok.extern.slf4j.Slf4j;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ApolloLoadNamespacePointcutAdvisor extends SimpleMethodInvokePointcutAdvisor {

    /**
     * @return
     * @see com.ctrip.framework.apollo.ConfigService#getConfig(java.lang.String)
     */
    public ApolloLoadNamespacePointcutAdvisor(ClassMethodInfo classMethodInfo) {
        super(classMethodInfo);
    }

    @Override
    protected void atBefore(InvokeVO invokeVO) {
        super.atBefore(invokeVO);
    }

    @Override
    protected void atExit(InvokeVO invokeVO) {
        super.atExit(invokeVO);
    }

    @Override
    protected void atExitAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        InitializedComponent.Children children = new InitializedComponent
                .Children(String.valueOf(invokeVO.getParams()[0]), invokeDetail.getDuration());

        applicationEventPublisher.publishEvent(new LoadApolloNamespaceEvent(this, children));
    }

}
