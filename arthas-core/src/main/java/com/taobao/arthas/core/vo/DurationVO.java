package com.taobao.arthas.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DurationVO {

    private transient BigDecimal startMillis;

    private transient BigDecimal endMillis;

    /**
     * Echarts数字值
     */
    @JSONField(name = "value")
    private BigDecimal duration;

    public DurationVO() {
        this.startMillis = DurationUtils.nowMillis();
    }

    public DurationVO(BigDecimal duration) {
        this.startMillis = BigDecimal.ZERO;
        this.endMillis = BigDecimal.ZERO;
        this.duration = duration;
    }

    public void initialized() {
        duration = DurationUtils.nowMillis().subtract(startMillis);
    }

}
