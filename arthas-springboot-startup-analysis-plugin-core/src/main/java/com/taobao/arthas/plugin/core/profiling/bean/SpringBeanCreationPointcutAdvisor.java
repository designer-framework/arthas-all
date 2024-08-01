package com.taobao.arthas.plugin.core.profiling.bean;

import com.taobao.arthas.api.advisor.AbstractMethodInvokePointcutAdvisor;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.constants.BeanCreateTag;
import com.taobao.arthas.plugin.core.events.BeanAopProxyCreatedEvent;
import com.taobao.arthas.plugin.core.events.BeanCreationEvent;
import com.taobao.arthas.plugin.core.events.BeanInitMethodInvokeEvent;
import com.taobao.arthas.plugin.core.events.SmartInstantiateSingletonEvent;
import com.taobao.arthas.plugin.core.utils.CreateBeanHolder;
import com.taobao.arthas.plugin.core.vo.BeanCreateVO;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

@Slf4j
public class SpringBeanCreationPointcutAdvisor extends AbstractMethodInvokePointcutAdvisor implements ApplicationListener<BeanCreationEvent>, DisposableBean, InitializingBean {

    private final SpringAgentStatisticsVO springAgentStatisticsResult;

    public SpringBeanCreationPointcutAdvisor(SpringAgentStatisticsVO springAgentStatisticsResult) {
        this.springAgentStatisticsResult = springAgentStatisticsResult;
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        BeanCreateVO creatingBean = new BeanCreateVO(invokeVO.getInvokeId(), String.valueOf(invokeVO.getParams()[0]));

        //采集已创建的Bean
        springAgentStatisticsResult.addCreatedBean(creatingBean);

        CreateBeanHolder.push(creatingBean);
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        //bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = CreateBeanHolder.pop();
        beanCreateVO.initialized();

        //完善已创建Bean的一些基本信息
        addBeanTags(invokeVO, beanCreateVO)
                //计算Bean创建耗时
                .calcBeanLoadTime();
    }

    private BeanCreateVO addBeanTags(InvokeVO invokeVO, BeanCreateVO creatingBean) {
        creatingBean.addTag(BeanCreateTag.threadName, Thread.currentThread().getName());
        creatingBean.addTag(BeanCreateTag.classLoader, getBeanClassLoader(invokeVO.getReturnObj()));
        creatingBean.addTag(BeanCreateTag.className, invokeVO.getReturnObj() == null ? null : invokeVO.getReturnObj().getClass().getName());
        return creatingBean;
    }

    private String getBeanClassLoader(Object returnBean) {
        if (returnBean != null) {

            ClassLoader classLoader = returnBean.getClass().getClassLoader();
            if (classLoader != null) {
                return classLoader.getClass().getName();
            } else {
                return "Bootstrap";
            }

        } else {

            return "Bootstrap";

        }
    }

    /**
     * AOP,InstantiateSingleton 等类型的耗时推送
     *
     * @param beanCreationEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(BeanCreationEvent beanCreationEvent) {
        if (beanCreationEvent instanceof SmartInstantiateSingletonEvent) {

            SmartInstantiateSingletonEvent smartInstantiateSingletonEvent = (SmartInstantiateSingletonEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            springAgentStatisticsResult.fillBeanCreate(smartInstantiateSingletonEvent.getBeanName(), beanCreateVO -> {

                beanCreateVO.addTag(BeanCreateTag.smartInitializingDuration, smartInstantiateSingletonEvent.getDuration());

            });

        } else if (beanCreationEvent instanceof BeanAopProxyCreatedEvent) {

            BeanAopProxyCreatedEvent beanAopProxyCreatedEvent = (BeanAopProxyCreatedEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            springAgentStatisticsResult.fillBeanCreate(beanAopProxyCreatedEvent.getBeanName(), beanCreateVO -> {

                beanCreateVO.addTag(BeanCreateTag.createProxyDuration, beanAopProxyCreatedEvent.getDuration());
                beanCreateVO.addTag(BeanCreateTag.proxiedClassName, beanAopProxyCreatedEvent.getProxiedClassName());

            });

        } else if (beanCreationEvent instanceof BeanInitMethodInvokeEvent) {

            BeanInitMethodInvokeEvent beanInitMethodInvokeEvent = (BeanInitMethodInvokeEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            springAgentStatisticsResult.fillBeanCreate(beanInitMethodInvokeEvent.getBeanName(), beanCreateVO -> {

                beanCreateVO.addTag(BeanCreateTag.initMethodDuration, beanInitMethodInvokeEvent.getDuration());

            });

        } else {

            log.warn("Source: {}, BeanName: {}", beanCreationEvent.getSource(), beanCreationEvent.getBeanName());

        }
    }

    @Override
    public void destroy() {
        CreateBeanHolder.release();
    }

}
