package com.taobao.arthas.plugin.core.enums;

public enum BeanLifeCycleEnum {
    /**
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    afterPropertiesSet,
    /**
     * @see org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)
     */
    createAopProxyClass,
    /**
     * @see org.springframework.beans.factory.SmartInitializingSingleton#afterSingletonsInstantiated()
     */
    afterSingletonsInstantiated
}
