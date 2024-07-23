package com.taobao.arthas.spring.utils;

import com.taobao.arthas.spring.vo.BeanCreateVO;
import org.springframework.core.NamedThreadLocal;

import java.util.Stack;

public class CreateBeanHolder {

    private static final BeanCreateVO NONE = new BeanCreateVO(-1, "NONE");

    private static final ThreadLocal<Stack<BeanCreateVO>> STACK = NamedThreadLocal.withInitial(Stack::new);

    public static BeanCreateVO pop() {
        Stack<BeanCreateVO> beanCreateVOS = STACK.get();
        if (beanCreateVOS.isEmpty()) {
            return NONE;
        } else {
            return beanCreateVOS.pop();
        }
    }

    public static void release() {
        STACK.remove();
    }

    public static BeanCreateVO peek() {
        Stack<BeanCreateVO> beanCreateVOS = STACK.get();
        if (beanCreateVOS.isEmpty()) {
            return NONE;
        } else {
            return beanCreateVOS.peek();
        }
    }

    public static void push(BeanCreateVO creatingBean) {

        Stack<BeanCreateVO> beanCreateStack = STACK.get();
        //子Bean
        if (!beanCreateStack.isEmpty()) {

            BeanCreateVO parentBeanCreateVO = beanCreateStack.peek();
            parentBeanCreateVO.addDependBean(creatingBean);

            //入栈
            beanCreateStack.push(creatingBean);

            //父Bean
        } else {

            //入栈
            beanCreateStack.push(creatingBean);

        }

    }

}
