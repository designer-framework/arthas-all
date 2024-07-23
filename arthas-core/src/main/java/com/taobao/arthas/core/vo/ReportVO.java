package com.taobao.arthas.core.vo;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-12 21:17
 */
public class ReportVO {

    private String key;
    private Object value;

    public ReportVO(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }

}
