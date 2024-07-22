package com.taobao.arthas.spring.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 01:10
 */
@Data
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
    private final List<BeanCreateVO> children;

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
    private long duration;
    /**
     * 实际加载耗时(减去依赖Bean的耗时)
     */
    private long actualDuration;

    /**
     * 创建代理Bean耗时
     */
    private Map<String, Object> tags;

    public BeanCreateVO(long id, String name) {
        this.id = id;
        this.name = name;
        startMillis = System.currentTimeMillis();
        children = new ArrayList<>();
        tags = new HashMap<>();
    }


    public void calcBeanLoadTime() {
        endMillis = System.currentTimeMillis();
        duration = endMillis - startMillis;
        long childrenDuration = children.stream().mapToLong(BeanCreateVO::getDuration).sum();
        actualDuration = duration - childrenDuration;
    }

    public void addDependBean(BeanCreateVO dependBean) {
        dependBean.parentId = id;
        children.add(dependBean);
    }

    /**
     * 加载SmartInitializingBean耗时: smartInitializingDuration
     * 生成代理Bean的耗时: createProxyDuration
     * 创建Bean的线程名(不出意外是main): threadName
     * 最终Bean的类名(如被aop代理, 则是代理类名): className
     * 创建Bean的类加载器: classLoader
     *
     * @param tagKey
     * @param tagValue
     */
    public void addTag(String tagKey, Object tagValue) {
        tags.put(tagKey, tagValue);
    }

}
