package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.BeanAopProxyCreatedEvent;
import com.taobao.arthas.spring.events.BeanCreationEvent;
import com.taobao.arthas.spring.events.InstantiateSingletonOverEvent;
import com.taobao.arthas.spring.profiling.AbstractInvokeAdviceHandler;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class SpringBeanCreateAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate, ApplicationListener<BeanCreationEvent>, DisposableBean {

    private final ThreadLocal<Stack<BeanCreateVO>> createBeanStack = ThreadLocal.withInitial(Stack::new);

    @Autowired
    private ProfilingResultVO profilingResultVO;

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    public SpringBeanCreateAdviceHandler() {
        super(FullyQualifiedClassUtils.parserClassMethodInfo(
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

        //采集已创建的Bean
        profilingResultVO.addCreatedBean(creatingBean);

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

    private void addBeanTags(InvokeVO invokeVO, BeanCreateVO creatingBean) {
        creatingBean.addTag("threadName", Thread.currentThread().getName());
        creatingBean.addTag("className", invokeVO.getReturnObj() == null ? null : invokeVO.getReturnObj().getClass().getName());
        creatingBean.addTag("classLoader", getBeanClassLoader(invokeVO.getReturnObj()));
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
     * 创建Bean成功, 出栈
     */
    @Override
    protected void atExit(InvokeVO invokeVO) {
        // bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = createBeanStack.get().pop();

        //Tags
        addBeanTags(invokeVO, beanCreateVO);

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
            profilingResultVO.fillBeanCreate(instantiateSingletonOverEvent.getBeanName(), beanCreateVO -> {

                beanCreateVO.addTag("smartInitializingDuration", instantiateSingletonOverEvent.getCostTime());

            });

        } else if (beanCreationEvent instanceof BeanAopProxyCreatedEvent) {

            BeanAopProxyCreatedEvent beanAopProxyCreatedEvent = (BeanAopProxyCreatedEvent) beanCreationEvent;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            profilingResultVO.fillBeanCreate(beanAopProxyCreatedEvent.getBeanName(), beanCreateVO -> {

                beanCreateVO.addTag("createProxyDuration", beanAopProxyCreatedEvent.getCostTime());

            });

        }

    }

    @Override
    public void destroy() throws Exception {
        createBeanStack.remove();
    }

}
