package com.taobao.arthas.spring.profiling;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.events.BeanCreatedEvent;
import com.taobao.arthas.spring.utils.FullyQualifiedClassUtils;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class SpringBeanCreateAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final TraceMethodInfo traceMethodInfo = FullyQualifiedClassUtils.toTraceMethodInfo(
            "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#createBean(" +
                    "java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[]" +
                    ")"
    );

    private final ThreadLocal<Stack<BeanCreateVO>> beanCreateStackThreadLocal = ThreadLocal.withInitial(Stack::new);

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public SpringBeanCreateAdviceHandler() {
        //traceMethods = arthasProperties.traceMethods();
    }

    @Override
    public boolean isCandidateClass(String className) {
        return traceMethodInfo.isCandidateClass(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        return traceMethodInfo.isCandidateMethod(methodName, methodArgTypes);
    }

    /**
     * 创建Bean, 入栈
     *
     * @param invokeVO
     */
    @Override
    public void atBefore(InvokeVO invokeVO) {
        BeanCreateVO dependBean = new BeanCreateVO(invokeVO.getInvokeId(), String.valueOf(invokeVO.getParams()[0]));

        //依赖Bean
        if (!beanCreateStackThreadLocal.get().isEmpty()) {

            BeanCreateVO parentBeanCreateVO = beanCreateStackThreadLocal.get().peek();
            parentBeanCreateVO.addDependBean(dependBean);

        }

        //入栈
        beanCreateStackThreadLocal.get().push(dependBean);
    }

    /**
     * 创建Bean成功, 出栈
     *
     * @param invokeVO {@link com.taobao.arthas.spring.report.BeanCreateReporter}
     */
    protected void atExit(InvokeVO invokeVO) {

        // bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = beanCreateStackThreadLocal.get().pop();
        beanCreateVO.calcBeanLoadTime();

        //发布Bean创建成功事件
        applicationEventPublisher.publishEvent(new BeanCreatedEvent(this));

    }

}
