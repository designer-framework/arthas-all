package com.taobao.arthas.plugin.core.vo;

import lombok.Data;
import org.springframework.core.Ordered;

/**
 * @description:
 * @author: Designer
 * @date : 2024-08-03 15:29
 */
@Data
public class StartUpLabelVO implements Ordered {

    private final int order;

    private final String label;

    private final Object value;

    public StartUpLabelVO(int order, String label, Object value) {
        this.order = order;
        this.label = label;
        this.value = value;
    }

}
