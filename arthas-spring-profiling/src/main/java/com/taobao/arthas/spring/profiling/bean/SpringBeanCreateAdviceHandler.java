package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.advisor.MatchCandidate;
import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.profiling.AbstractInvokeAdviceHandler;
import com.taobao.arthas.spring.properties.ArthasProperties;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.TraceMethodInfo;
import org.springframework.stereotype.Component;

import java.util.Stack;

@Component
public class SpringBeanCreateAdviceHandler extends AbstractInvokeAdviceHandler implements InvokeAdviceHandler, MatchCandidate {

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final TraceMethodInfo traceMethodInfo = new TraceMethodInfo(
            "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory"
            , "doCreateBean"
            , new String[]{"java.lang.String", "org.springframework.beans.factory.support.RootBeanDefinition", "java.lang.Object[]"}
    );

    private final ThreadLocal<Stack<BeanCreateVO>> beanCreateStackThreadLocal = ThreadLocal.withInitial(Stack::new);

    public SpringBeanCreateAdviceHandler(ArthasProperties arthasProperties) {
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
     * Bean创建成功, 出栈
     *
     * @param invokeVO
     */
    protected void atExit(InvokeVO invokeVO) {

        // bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = beanCreateStackThreadLocal.get().pop();
        beanCreateVO.calcBeanLoadTime();

    }

}
