package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.BeanAopProxyCreatedEvent;
import com.taobao.arthas.spring.events.BeanCreationEvent;
import com.taobao.arthas.spring.events.InstantiateSingletonOverEvent;
import com.taobao.arthas.spring.listener.BeanCreateReporter;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.ReportVO;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

@Component
public class SpringBeanCreateAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, ApplicationListener<BeanCreationEvent>, BeanCreateReporter<Collection<BeanCreateVO>> {

    private final ThreadLocal<Stack<BeanCreateVO>> createBeanStack = ThreadLocal.withInitial(Stack::new);

    private final MultiValueMap<String, BeanCreateVO> createdMap = new LinkedMultiValueMap<>();

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public SpringBeanCreateAdviceHandler() {
        super(FullyQualifiedClassUtils.toTraceMethodInfo(
                "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory" +
                        "#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])"
        ));
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        BeanCreateVO creatingBean = new BeanCreateVO(invokeVO.getInvokeId(), String.valueOf(invokeVO.getParams()[0]));

        createdMap.add(creatingBean.getName(), creatingBean);

        //子Bean
        if (!createBeanStack.get().isEmpty()) {

            BeanCreateVO parentBeanCreateVO = createBeanStack.get().peek();
            parentBeanCreateVO.addDependBean(creatingBean);
            //入栈
            createBeanStack.get().push(creatingBean);

            //父Bean
        } else {

            //入栈
            createBeanStack.get().push(creatingBean);

        }
    }

    /**
     * 创建Bean成功, 出栈
     *
     * @param invokeVO {@link com.taobao.arthas.spring.listener.BeanCreateReporter}
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        // bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = createBeanStack.get().pop();
        //计算Bean创建耗时
        beanCreateVO.calcBeanLoadTime();
    }

    /**
     * AOP,InstantiateSingleton 等类型的耗时推送
     *
     * @param beanCreationEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(BeanCreationEvent beanCreationEvent) {

        if (beanCreationEvent instanceof InstantiateSingletonOverEvent) {

            InstantiateSingletonOverEvent instantiateSingletonOverEvent = (InstantiateSingletonOverEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            List<BeanCreateVO> beanCreates = createdMap.get(instantiateSingletonOverEvent.getBeanName());
            if (beanCreates != null && !beanCreates.isEmpty()) {
                beanCreates.get(beanCreates.size() - 1).setSmartInitializingLoadMillis(instantiateSingletonOverEvent.getCostTime());
            }

        } else if (beanCreationEvent instanceof BeanAopProxyCreatedEvent) {

            BeanAopProxyCreatedEvent beanAopProxyCreatedEvent = (BeanAopProxyCreatedEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            List<BeanCreateVO> beanCreates = createdMap.get(beanAopProxyCreatedEvent.getBeanName());
            if (beanCreates != null && !beanCreates.isEmpty()) {
                beanCreates.get(beanCreates.size() - 1).setAopProxyLoadMillis(beanAopProxyCreatedEvent.getCostTime());
            }

        }

    }

    @Override
    public ReportVO getReportVO() {
        return new ReportVO("CreatedBeans", createdMap.toSingleValueMap().values());
    }

    @Override
    public void release() {
        createBeanStack.remove();
    }

}
