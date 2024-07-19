package com.taobao.arthas.core.config;

import com.taobao.arthas.core.util.reflect.ArthasReflectUtils;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static java.lang.reflect.Modifier.isStatic;

/**
 * <pre>
 * 配置类。
 * 注意本类里的所有字段不能有默认值，否则会出现配置混乱。
 * 在 com.taobao.arthas.core.Arthas#attach 里会调用 Configure#toStrig
 * <pre>
 *
 * @author vlinux
 * @author hengyunabc 2018-11-12
 */
@Data
@Config(prefix = "arthas")
public class Configure {

    private String ip;

    private Integer httpPort;

    private String arthasCore;

    private String arthasAgent;

    /**
     * @see com.taobao.arthas.common.ArthasConstants#ARTHAS_OUTPUT
     */
    private String outputPath;

    /**
     * 需要被增强的ClassLoader的全类名，多个用英文 , 分隔
     */
    private String enhanceLoaders;

    /**
     * <pre>
     * 1. 如果显式传入 arthas.agentId ，则直接使用
     * 2. 如果用户没有指定，则自动尝试在查找应用的 appname，加为前缀，比如 system properties设置 project.name是 demo，则
     *    生成的 agentId是  demo-xxxx
     * </pre>
     */
    private String appName;

    /**
     * 序列化成字符串
     *
     * @return 序列化字符串
     */
    @Override
    public String toString() {

        Map<String, String> map = new HashMap<String, String>();
        for (Field field : ArthasReflectUtils.getFields(Configure.class)) {

            // 过滤掉静态类
            if (isStatic(field.getModifiers())) {
                continue;
            }

            // 非静态的才需要纳入非序列化过程
            try {
                Object fieldValue = ArthasReflectUtils.getFieldValueByField(this, field);
                if (fieldValue != null) {
                    map.put(field.getName(), String.valueOf(fieldValue));
                }
            } catch (Throwable t) {
                //
            }

        }

        return FeatureCodec.DEFAULT_COMMANDLINE_CODEC.toString(map);
    }

}
