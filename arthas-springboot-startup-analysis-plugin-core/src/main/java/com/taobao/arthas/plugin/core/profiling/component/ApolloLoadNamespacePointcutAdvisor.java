package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
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

    /**
     * @param invokeVO
     * @param invokeDetail
     */
    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO invokeDetail) {
        InitializedComponent.Children children = new InitializedComponent
                .Children(SpringComponentEnum.APOLLO, String.valueOf(invokeVO.getParams()[0]), invokeDetail.getDuration());

        applicationEventPublisher.publishEvent(new LoadApolloNamespaceEvent(this, children));
    }

}
