package com.taobao.arthas.core.container.listener;

import com.taobao.arthas.core.container.InvokeListener;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-08 00:36
 */
public class SpringInvokeListenerFactory implements InvokeListenerFactory {

    @Autowired
    private List<InvokeListener> invokeListeners;

    @Override
    public void atEnter() {
    }

}
