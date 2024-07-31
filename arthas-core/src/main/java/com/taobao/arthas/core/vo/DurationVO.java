package com.taobao.arthas.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class DurationVO {

    private BigDecimal startMillis;

    private BigDecimal endMillis;

    /**
     * Echarts数字值
     */
    @JSONField(name = "value")
    private BigDecimal duration;

    public DurationVO() {
        this.startMillis = DurationUtils.nowMillis();
    }

    public DurationVO(BigDecimal duration) {
        this.startMillis = DurationUtils.nowMillis();
        this.endMillis = DurationUtils.nowMillis();
        this.duration = duration;
    }

    public void initialized() {
        //结束时间
        setEndMillis(DurationUtils.nowMillis())
                //耗时
                .setDuration(getEndMillis().subtract(startMillis));
    }

}
