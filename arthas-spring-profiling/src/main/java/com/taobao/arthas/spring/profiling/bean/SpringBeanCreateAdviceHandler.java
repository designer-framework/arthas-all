package com.taobao.arthas.spring.profiling.bean;

import com.taobao.arthas.profiling.api.handler.InvokeAdviceHandler;
import com.taobao.arthas.profiling.api.vo.InvokeVO;
import com.taobao.arthas.spring.properties.ArthasProperties;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.TraceMethodProperty;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.Stack;

@Component
public class SpringBeanCreateAdviceHandler implements InvokeAdviceHandler {

    /**
     * org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory#doCreateBean(java.lang.String, org.springframework.beans.factory.support.RootBeanDefinition, java.lang.Object[])
     */
    private final String[] methodArgTypes = new String[]{"java.lang.String", "org.springframework.beans.factory.support.RootBeanDefinition", "java.lang.Object[]"};

    private final Set<TraceMethodProperty> traceMethods;

    private final ThreadLocal<Stack<BeanCreateVO>> beanCreateStackThreadLocal = ThreadLocal.withInitial(Stack::new);

    public SpringBeanCreateAdviceHandler(ArthasProperties arthasProperties) {
        traceMethods = arthasProperties.traceMethods();
    }

    @Override
    public boolean isCandidateClass(String className) {
        return "org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory".equals(className);
    }

    @Override
    public boolean isCandidateMethod(String className, String methodName, String[] methodArgTypes) {
        //
        if (!"doCreateBean".equals(methodName)) {
            return false;
        }

        for (int i = 0; i < this.methodArgTypes.length; i++) {
            if (!this.methodArgTypes[i].equals(methodArgTypes[i])) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void before(InvokeVO invokeVO) {
        BeanCreateVO dependBean = new BeanCreateVO(invokeVO.getInvokeId(), String.valueOf(invokeVO.getParams()[0]));

        //依赖Bean
        if (!beanCreateStackThreadLocal.get().isEmpty()) {

            BeanCreateVO parentBeanCreateVO = beanCreateStackThreadLocal.get().peek();
            parentBeanCreateVO.addDependBean(dependBean);

        }

        //入栈
        beanCreateStackThreadLocal.get().push(dependBean);
    }

    @Override
    public void afterReturning(InvokeVO invokeVO) {
        onExit(invokeVO);
    }

    @Override
    public void afterThrowing(InvokeVO invokeVO) {
        onExit(invokeVO);
    }

    /**
     * Bean创建成功, 出栈
     *
     * @param invokeVO
     */
    private void onExit(InvokeVO invokeVO) {
        // bean初始化结束, 出栈
        BeanCreateVO beanCreateVO = beanCreateStackThreadLocal.get().pop();
        beanCreateVO.calcBeanLoadTime();
    }

}
