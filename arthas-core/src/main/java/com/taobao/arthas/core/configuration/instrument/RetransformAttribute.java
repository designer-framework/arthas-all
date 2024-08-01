package com.taobao.arthas.core.configuration.instrument;

import lombok.Data;

@Data
public class RetransformAttribute {

    /**
     * 需要被修改的类
     *
     * @return
     */
    private String className;

    /**
     * 目标样
     * <p>
     * {@link com.alibaba.bytekit.agent.inst.Instrument 类上必须添加该注解}
     */
    private Class<?> instrumentClass;

}
