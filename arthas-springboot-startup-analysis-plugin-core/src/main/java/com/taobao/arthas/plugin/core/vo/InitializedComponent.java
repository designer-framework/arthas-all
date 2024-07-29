package com.taobao.arthas.plugin.core.vo;

import com.taobao.arthas.core.vo.DurationUtils;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InitializedComponent {

    private final ComponentEnum componentEnum;

    private final String subName;

    private final BigDecimal startTime;

    private BigDecimal costTime;

    private BigDecimal endTime;

    public InitializedComponent(ComponentEnum componentEnum) {
        this(componentEnum, null);
    }

    public InitializedComponent(ComponentEnum componentEnum, String subName) {
        this.startTime = DurationUtils.nowMillis();
        this.componentEnum = componentEnum;
        this.subName = subName;
    }

    public InitializedComponent initialized() {
        endTime = DurationUtils.nowMillis();
        costTime = endTime.subtract(startTime);
        return this;
    }

}
