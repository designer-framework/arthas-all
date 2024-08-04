package com.taobao.arthas.plugin.core.profiling.statistics;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.taobao.arthas.plugin.core.constants.BeanCreateTag;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import com.taobao.arthas.plugin.core.vo.*;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-04 18:23
 */
public class ComponentsMetricStatisticsBuilder implements StatisticsBuilder {

    @Override
    public StatisticsInfo build(SpringAgentStatistics springAgentStatistics) {
        return new SimpleStatisticsInfo("componentsMetric", getComponentsMetric(springAgentStatistics));
    }

    private InitializedComponentsMetric getComponentsMetric(SpringAgentStatistics statistics) {
        //构建报表
        InitializedComponentsMetric rootMetric = createRootMetric(SpringComponentEnum.SPRING_APPLICATION, statistics.getAgentTime());

        //InitDestroyBean
        rootMetric.addChildren(createInitMethodDurationBeanMetric(statistics.getCreatedBeans()));

        //各组件耗时统计, 如: Apollo, Swagger
        Collection<InitializedComponent> initializedComponents = statistics.getInitializedComponents();
        initializedComponents.forEach(initializedComponent -> {
            if (initializedComponent.isLazyRoot()) {
                initializedComponent.updateDurationByChildren();
            }
        });
        rootMetric.addChildren(JSONObject.parseObject(
                JSON.toJSONString(initializedComponents), new TypeReference<List<InitializedComponentsMetric>>() {
                }
        ));

        return fillComponentMetric(rootMetric, true);
    }

    /**
     * 组件根节点
     *
     * @param showName
     * @param duration
     * @return
     */
    public InitializedComponentsMetric createRootMetric(SpringComponentEnum showName, BigDecimal duration) {
        return new InitializedComponentsMetric(showName.getShowName(), duration);
    }

    /**
     * 组件子节点
     *
     * @param showName
     * @param duration
     * @return
     */
    public InitializedComponentsMetric createMetricItem(String showName, BigDecimal duration) {
        return new InitializedComponentsMetric(showName, duration);
    }

    public InitializedComponentsMetric createInitMethodDurationBeanMetric(Collection<BeanCreateVO> createBeans) {
        //SmartInitializingBean
        List<BeanCreateVO> smartInitializingBean = createBeans.stream()
                .filter(beanCreateVO -> beanCreateVO.getTags().get(BeanCreateTag.initMethodDuration) != null)
                .collect(Collectors.toList());

        //totalCost
        BigDecimal totalCost = smartInitializingBean.stream()
                .map(beanCreateVO -> (BigDecimal) beanCreateVO.getTags().get(BeanCreateTag.initMethodDuration))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //构建SmartInitializingBean根节点
        InitializedComponentsMetric initAnnotationBeanMetric = createRootMetric(SpringComponentEnum.INIT_DESTROY_ANNOTATION_BEAN, totalCost);
        smartInitializingBean.forEach((beanCreateVO) -> {

            Object className = beanCreateVO.getTags().get(BeanCreateTag.className);
            Object initMethods = beanCreateVO.getTags().get(BeanCreateTag.initMethodName);
            BigDecimal createProxyDuration = (BigDecimal) beanCreateVO.getTags().get(BeanCreateTag.initMethodDuration);
            initAnnotationBeanMetric.addChildren(createMetricItem(className + "," + initMethods, createProxyDuration));

        });

        return initAnnotationBeanMetric;
    }

    /**
     * SpringBean-Aop耗时统计
     *
     * @return
     */

    public InitializedComponentsMetric fillComponentMetric(InitializedComponentsMetric rootMetric, boolean isRootMetric) {
        //根节点
        if (isRootMetric) {
            rootMetric.fillOthersDuration();
        }

        List<InitializedComponentsMetric> children = rootMetric.getChildren();
        if (!CollectionUtils.isEmpty(children)) {

            //计算耗时百分比
            calcPercentage(children);

            for (InitializedComponentsMetric child : children) {

                //有组件明细
                if (!CollectionUtils.isEmpty(child.getChildren())) {

                    //明细耗时统计
                    //child.fillOthersDuration();

                    //遍历tree
                    fillComponentMetric(child, false);

                }

            }

        }

        return rootMetric;
    }

    private void calcPercentage(List<InitializedComponentsMetric> children) {
        BigDecimal remaining = BigDecimal.ONE;
        BigDecimal sum = children.stream().map(InitializedComponentsMetric::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (InitializedComponentsMetric child : children) {

            //还有剩余
            if (remaining.compareTo(BigDecimal.ZERO) > 0) {

                BigDecimal percent = child.getDuration().divide(sum, 3, RoundingMode.HALF_UP);
                child.setPercent(percent);
                remaining = remaining.subtract(percent);

            } else {

                child.setPercent(BigDecimal.ZERO);

            }
        }

    }

}
