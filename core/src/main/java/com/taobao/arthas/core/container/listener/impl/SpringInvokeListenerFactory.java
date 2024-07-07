package com.taobao.arthas.core.container.listener.impl;

import com.taobao.arthas.core.container.listener.InvokeListener;
import com.taobao.arthas.core.container.listener.InvokeListenerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 00:36
 */
@Component
public class SpringInvokeListenerFactory implements InvokeListenerFactory {

    @Autowired
    private List<InvokeListener> invokeListeners;

    @Override
    public void atEnter() {
    }

}
