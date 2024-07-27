package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private BigDecimal startMillis;
    /**
     * 创建完成时间
     */
    private BigDecimal endMillis;
    /**
     * 加载耗时
     */
    private BigDecimal duration;
    /**
     * 实际加载耗时(减去依赖Bean的耗时)
     */
    private BigDecimal actualDuration;

    /**
     * 创建代理Bean耗时
     */
    private Map<String, Object> tags;

    public BeanCreateVO(long id, String name) {
        this.id = id;
        this.name = name;
        startMillis = DurationUtils.nowMillis();
        children = new ArrayList<>();
        tags = new HashMap<>();
    }


    public void calcBeanLoadTime() {
        endMillis = DurationUtils.nowMillis();
        duration = endMillis.subtract(startMillis);

        BigDecimal childrenDuration = children.stream().map(BeanCreateVO::getDuration)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        actualDuration = duration.subtract(childrenDuration);
    }

    public void addDependBean(BeanCreateVO dependBean) {
        dependBean.parentId = id;
        children.add(dependBean);
    }

    /**
     * 1.
     * -> 加载创建Bean自身的总耗时: actualDuration
     * ---> 生成代理Bean的耗时: createProxyDuration
     * 2.
     * -> 加载SmartInitializingBean耗时: smartInitializingDuration
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
