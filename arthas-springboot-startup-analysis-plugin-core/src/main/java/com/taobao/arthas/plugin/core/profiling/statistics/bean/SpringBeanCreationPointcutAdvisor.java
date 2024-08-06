package com.taobao.arthas.plugin.core.profiling.statistics.bean;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.core.advisor.SimpleMethodInvokePointcutAdvisor;
import com.taobao.arthas.core.vo.MethodInvokeVO;
import com.taobao.arthas.plugin.core.constants.BeanCreateTag;
import com.taobao.arthas.plugin.core.enums.BeanLifeCycleEnum;
import com.taobao.arthas.plugin.core.events.BeanAopProxyCreatedLifeCycleEvent;
import com.taobao.arthas.plugin.core.events.BeanCreationLifeCycleEvent;
import com.taobao.arthas.plugin.core.events.BeanInitMethodInvokeLifeCycleEvent;
import com.taobao.arthas.plugin.core.events.SmartInstantiateSingletonLifeCycleEvent;
import com.taobao.arthas.plugin.core.utils.CreateBeanHolder;
import com.taobao.arthas.plugin.core.vo.BeanCreateVO;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationListener;

@Slf4j
public class SpringBeanCreationPointcutAdvisor extends SimpleMethodInvokePointcutAdvisor implements ApplicationListener<BeanCreationLifeCycleEvent>, DisposableBean, InitializingBean {

    private final SpringAgentStatistics springAgentStatistics;

    public SpringBeanCreationPointcutAdvisor(ClassMethodInfo classMethodInfo, SpringAgentStatistics springAgentStatistics) {
        super(classMethodInfo);
        this.springAgentStatistics = springAgentStatistics;
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    protected void atMethodInvokeBefore(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeBefore(invokeVO, methodInvokeVO);
        BeanCreateVO creatingBean = new BeanCreateVO(invokeVO.getInvokeId(), String.valueOf(invokeVO.getParams()[0]));

        //采集已创建的Bean
        springAgentStatistics.addCreatedBean(creatingBean);

        CreateBeanHolder.push(creatingBean);
    }

    /**
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atMethodInvokeAfter(InvokeVO invokeVO, MethodInvokeVO methodInvokeVO) {
        super.atMethodInvokeAfter(invokeVO, methodInvokeVO);
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
     * @param beanCreationLifeCycleEvent the event to respond to
     */
    @Override
    public void onApplicationEvent(BeanCreationLifeCycleEvent beanCreationLifeCycleEvent) {
        if (beanCreationLifeCycleEvent instanceof SmartInstantiateSingletonLifeCycleEvent) {

            SmartInstantiateSingletonLifeCycleEvent smartInstantiateSingletonEvent = (SmartInstantiateSingletonLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(smartInstantiateSingletonEvent.getBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(BeanLifeCycleEnum.afterSingletonsInstantiated, smartInstantiateSingletonEvent.getLifeCycleDurations());
            });

        } else if (beanCreationLifeCycleEvent instanceof BeanAopProxyCreatedLifeCycleEvent) {

            BeanAopProxyCreatedLifeCycleEvent beanAopProxyCreatedEvent = (BeanAopProxyCreatedLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(beanAopProxyCreatedEvent.getBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(BeanLifeCycleEnum.createAopProxyClass, beanAopProxyCreatedEvent.getLifeCycleDurations());
            });

        } else if (beanCreationLifeCycleEvent instanceof BeanInitMethodInvokeLifeCycleEvent) {

            BeanInitMethodInvokeLifeCycleEvent beanInitMethodInvokeEvent = (BeanInitMethodInvokeLifeCycleEvent) beanCreationLifeCycleEvent;

            springAgentStatistics.fillBeanCreate(beanInitMethodInvokeEvent.getBeanName(), beanCreateVO -> {
                beanCreateVO.addBeanLifeCycle(BeanLifeCycleEnum.afterPropertiesSet, beanInitMethodInvokeEvent.getLifeCycleDurations());
            });

        } else {

            log.warn("Source: {}, BeanName: {}", beanCreationLifeCycleEvent.getSource(), beanCreationLifeCycleEvent.getBeanName());

        }
    }

    @Override
    public void destroy() {
        CreateBeanHolder.release();
    }

}
