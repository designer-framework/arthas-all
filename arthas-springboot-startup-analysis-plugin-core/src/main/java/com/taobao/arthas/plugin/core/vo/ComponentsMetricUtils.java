package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.plugin.core.constants.BeanCreateTag;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentsMetricUtils {

    /**
     * 组件根节点
     *
     * @param showName
     * @param totalCost
     * @return
     */
    static InitializedComponentsMetric createRootMetric(SpringComponentEnum showName, BigDecimal totalCost) {
        return new InitializedComponentsMetric(showName.getShowName(), totalCost);
    }

    /**
     * 组件子节点
     *
     * @param showName
     * @param itemCost
     * @return
     */
    static InitializedComponentsMetric createMetricItem(String showName, BigDecimal itemCost) {
        return new InitializedComponentsMetric(showName, itemCost);
    }

    /**
     * SpringBean-Aop耗时统计
     *
     * @return
     */
    static InitializedComponentsMetric createAopProxyComponentMetric(Map<String, BeanCreateVO> createdBeansMap) {
        //被Aop代理的Bean
        List<BeanCreateVO> aopProxyBean = createdBeansMap.values().stream()
                .filter(beanCreateVO -> beanCreateVO.getTags().get(BeanCreateTag.createProxyDuration) != null)
                .collect(Collectors.toList());

        //Aop创建代理Bean的总耗时
        BigDecimal totalCost = aopProxyBean.stream()
                .map(beanCreateVO -> (BigDecimal) beanCreateVO.getTags().get(BeanCreateTag.createProxyDuration))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //构建AOP组件根节点
        InitializedComponentsMetric aopProxyCreatorMetric = createRootMetric(SpringComponentEnum.AOP_PROXY_CREATOR, totalCost);

        //填充Aop创建代理Bean的所有明细
        aopProxyBean.forEach((beanCreateVO) -> {

            Object originClassName = beanCreateVO.getTags().get(BeanCreateTag.className);
            Object proxiedClassName = beanCreateVO.getTags().get(BeanCreateTag.proxiedClassName);
            BigDecimal createProxyDuration = (BigDecimal) beanCreateVO.getTags().get(BeanCreateTag.createProxyDuration);
            aopProxyCreatorMetric.addChildren(createMetricItem(originClassName + "-->" + proxiedClassName, createProxyDuration));

        });

        return aopProxyCreatorMetric;
    }

    /**
     * SpringBean-Aop耗时统计
     *
     * @return
     */

    static InitializedComponentsMetric fillComponentMetric(InitializedComponentsMetric rootMetric) {
        //根节点
        if (rootMetric.isRoot()) {
            rootMetric.fillOthersDuration();
        }

        List<InitializedComponentsMetric> children = rootMetric.getChildren();
        if (!CollectionUtils.isEmpty(children)) {

            for (InitializedComponentsMetric child : children) {

                //有组件明细
                if (!CollectionUtils.isEmpty(child.getChildren())) {

                    //明细耗时统计
                    child.fillOthersDuration();

                    //遍历孙级
                    fillComponentMetric(child);

                }

            }

        }

        return rootMetric;
    }

}