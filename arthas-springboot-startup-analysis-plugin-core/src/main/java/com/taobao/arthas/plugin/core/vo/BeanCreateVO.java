package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.DurationVO;
import com.taobao.arthas.plugin.core.enums.BeanLifeCycleEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 01:10
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class BeanCreateVO extends DurationVO {

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
    private List<BeanCreateVO> children;

    /**
     * parentId
     */
    private long parentId;
    /**
     * 实际加载耗时(减去创建依赖Bean的耗时)
     */
    private BigDecimal actualDuration;
    /**
     *
     */
    private Map<BeanLifeCycleEnum, BeanLifeCycleDuration> beanLifeCycles;
    /**
     * 创建代理Bean耗时
     */
    private Map<String, Object> tags;

    public BeanCreateVO(long id, String name) {
        this.id = id;
        this.name = name;
        children = new ArrayList<>();
        tags = new HashMap<>();
    }

    @Override
    @JSONField(name = "duration")
    public BigDecimal getDuration() {
        return super.getDuration();
    }

    /**
     * 加载Bean的实际耗时
     *
     * @return
     */
    public BigDecimal getActualDuration() {
        Map<BeanLifeCycleEnum, BeanLifeCycleDuration> beanLifeCycles = getBeanLifeCycles();
        if (beanLifeCycles == null) {
            return actualDuration;
        }
        
        BigDecimal actualDuration = this.actualDuration;
        for (Map.Entry<BeanLifeCycleEnum, BeanLifeCycleDuration> entry : beanLifeCycles.entrySet()) {

            BeanLifeCycleEnum lifeCycleEnum = entry.getKey();
            BeanLifeCycleDuration beanLifeCycleDuration = entry.getValue();
            switch (lifeCycleEnum) {
                case createAopProxyClass:
                case afterPropertiesSet:
                    break;
                //统计耗时
                case afterSingletonsInstantiated:
                    actualDuration = actualDuration.add(beanLifeCycleDuration.getDuration());
                    break;
                default:
                    log.error("Unknown lifeCycleEnum: {}, {}", name, lifeCycleEnum);
                    break;
            }

        }

        return actualDuration;
    }

    public void calcBeanLoadTime() {
        BigDecimal childrenDuration = children.stream().map(BeanCreateVO::getDuration)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        actualDuration = getDuration().subtract(childrenDuration);
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

    public void addBeanLifeCycle(BeanLifeCycleEnum lifeCycleEnum, BeanLifeCycleDuration beanLifeCycleDuration) {
        if (beanLifeCycles == null) {
            beanLifeCycles = new LinkedHashMap<>();
        }
        beanLifeCycles.put(lifeCycleEnum, beanLifeCycleDuration);
    }

}
