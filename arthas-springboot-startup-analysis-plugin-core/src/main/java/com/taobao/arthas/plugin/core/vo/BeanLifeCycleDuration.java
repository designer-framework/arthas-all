package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.DurationVO;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BeanLifeCycleDuration extends DurationVO {

    private String stepName;

    protected BeanLifeCycleDuration(String stepName) {
        this.stepName = stepName;
    }

    /**
     * @param stepName
     * @param durationVO Bean创建及创建完成的时间
     * @return
     */
    public static BeanLifeCycleDuration create(String stepName, DurationVO durationVO) {
        BeanLifeCycleDuration beanLifeCycleDuration = new BeanLifeCycleDuration(stepName);
        beanLifeCycleDuration.copyDuration(durationVO);
        return beanLifeCycleDuration;
    }

    @Override
    @JSONField(name = "duration")
    public BigDecimal getDuration() {
        return super.getDuration();
    }

}