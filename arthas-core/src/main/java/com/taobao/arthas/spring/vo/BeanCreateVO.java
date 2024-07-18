package com.taobao.arthas.spring.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 01:10
 */
public class BeanCreateVO implements Serializable {

    /**
     * 当前Bean的ID
     */
    private final long id;

    /**
     * 当前bean的名字
     */
    private final String name;

    /**
     * 随着当前bean的初始化而加载的子bean
     */
    private final List<BeanCreateVO> dependBeans;

    /**
     * parentId
     */
    private long parentId;
    /**
     * 创建时间
     */
    private long startMillis;
    /**
     * 创建完成时间
     */
    private long endMillis;
    /**
     * 加载耗时
     */
    private long loadMillis;
    /**
     * 实际加载耗时(减去依赖Bean的耗时)
     */
    private long actualLoadMillis;
    /**
     * 加载耗时
     */
    private Long smartInitializingLoadMillis;
    /**
     * 创建代理Bean耗时
     */
    private Long aopProxyLoadMillis;

    public BeanCreateVO(long id, String name) {
        this.id = id;
        this.name = name;
        startMillis = System.currentTimeMillis();
        dependBeans = new ArrayList<>();
    }


    public void calcBeanLoadTime() {
        endMillis = System.currentTimeMillis();
        loadMillis = endMillis - startMillis;
        long childrenDuration = dependBeans.stream().mapToLong(BeanCreateVO::getLoadMillis).sum();
        actualLoadMillis = loadMillis - childrenDuration;
    }

    public List<BeanCreateVO> getDependBeans() {
        return dependBeans;
    }

    public void addDependBean(BeanCreateVO dependBean) {
        dependBean.parentId = id;
        dependBeans.add(dependBean);
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getParentId() {
        return parentId;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public long getEndMillis() {
        return endMillis;
    }

    public long getLoadMillis() {
        return loadMillis;
    }

    public long getActualLoadMillis() {
        return actualLoadMillis;
    }

    public Long getSmartInitializingLoadMillis() {
        return smartInitializingLoadMillis;
    }

    public void setSmartInitializingLoadMillis(Long smartInitializingLoadMillis) {
        this.smartInitializingLoadMillis = smartInitializingLoadMillis;
    }

    public Long getAopProxyLoadMillis() {
        return aopProxyLoadMillis;
    }

    public void setAopProxyLoadMillis(Long aopProxyLoadMillis) {
        this.aopProxyLoadMillis = aopProxyLoadMillis;
    }

}
