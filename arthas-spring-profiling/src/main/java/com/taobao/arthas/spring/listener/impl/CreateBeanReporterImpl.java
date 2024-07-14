package com.taobao.arthas.spring.listener.impl;

import com.taobao.arthas.spring.events.BeanCreatedEvent;
import com.taobao.arthas.spring.events.BeanCreatingEvent;
import com.taobao.arthas.spring.events.BeanCreationEvent;
import com.taobao.arthas.spring.events.InstantiateSingletonOverEvent;
import com.taobao.arthas.spring.listener.BeanCreateReporter;
import com.taobao.arthas.spring.vo.BeanCreateVO;
import com.taobao.arthas.spring.vo.ReportVO;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

@Component
public class CreateBeanReporterImpl implements BeanCreateReporter<Collection<BeanCreateVO>>, ApplicationListener<BeanCreationEvent> {

    private final ThreadLocal<Stack<BeanCreateVO>> beanCreateStackThreadLocal = ThreadLocal.withInitial(Stack::new);

    private final LinkedMultiValueMap<String, BeanCreateVO> beanCreateMap = new LinkedMultiValueMap<>();

    /**
     * @param event the event to respond to
     */
    @Override
    public void onApplicationEvent(BeanCreationEvent event) {
        //Bean创建事件
        if (event instanceof BeanCreatingEvent) {

            BeanCreateVO beanCreateVO = ((BeanCreatingEvent) event).getBeanCreateVO();
            beanCreateMap.add(beanCreateVO.getName(), beanCreateVO);

            //子Bean
            if (!beanCreateStackThreadLocal.get().isEmpty()) {

                BeanCreateVO parentBeanCreateVO = beanCreateStackThreadLocal.get().peek();
                parentBeanCreateVO.addDependBean(beanCreateVO);
                beanCreateStackThreadLocal.get().push(((BeanCreatingEvent) event).getBeanCreateVO());

                //父Bean
            } else {

                //入栈
                beanCreateStackThreadLocal.get().push(((BeanCreatingEvent) event).getBeanCreateVO());

            }


            //Bean创建完成事件
        } else if (event instanceof BeanCreatedEvent) {

            // bean初始化结束, 出栈
            BeanCreateVO beanCreateVO = beanCreateStackThreadLocal.get().pop();
            //计算Bean创建耗时
            beanCreateVO.calcBeanLoadTime();

            //单例Bean初始化事件
        } else if (event instanceof InstantiateSingletonOverEvent) {

            InstantiateSingletonOverEvent instantiateSingletonOverEvent = (InstantiateSingletonOverEvent) event;

            //多个同名Bean, 后面的会覆盖前面的, 所以取最后一个
            List<BeanCreateVO> beanCreates = beanCreateMap.get(instantiateSingletonOverEvent.getBeanName());
            if (beanCreates != null && !beanCreates.isEmpty()) {
                beanCreates.get(beanCreates.size() - 1).setSmartInitializingLoadMillis(instantiateSingletonOverEvent.getCostTime());
            }

        }

    }


    @Override
    public ReportVO getReportVO() {
        return new ReportVO() {
            @Override
            public String getTagKey() {
                return "CreatedBeans";
            }

            @Override
            public Object getValue() {
                return beanCreateMap.toSingleValueMap().values();
            }
        };
    }

    @Override
    public void release() {
        beanCreateStackThreadLocal.remove();
    }

}
