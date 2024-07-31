package com.taobao.arthas.plugin.core.events;

import com.taobao.arthas.core.vo.DurationUtils;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-14 12:28
 */
@Getter
public class ScanningCandidateComponentEvent extends BeanCreationEvent {

    private final String packageName;

    private final BigDecimal startTime;

    private BigDecimal endTime;

    private BigDecimal costTime;

    public ScanningCandidateComponentEvent(Object source, String packageName) {
        super(source);
        this.packageName = packageName;
        startTime = DurationUtils.nowMillis();
    }

    public ScanningCandidateComponentEvent instantiated() {
        endTime = DurationUtils.nowMillis();
        costTime = endTime.subtract(startTime);
        return this;
    }

}
