package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.DurationUtils;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Data
@Accessors(chain = true)
public class InitializedComponent {

    /**
     * 组件名
     */
    private final String componentName;

    /**
     * Echarts展示名
     */
    @JSONField(name = "name")
    private final String showName;

    private transient BigDecimal startMillis;

    /**
     * Echarts数字值
     */
    @JSONField(name = "value")
    private BigDecimal duration;

    private List<Children> children;

    private InitializedComponent(ComponentEnum componentName) {
        this.componentName = componentName.getComponentName();
        this.showName = componentName.getShowName();
        this.startMillis = DurationUtils.nowMillis();
    }

    /**
     * 两层结构, 便于Echarts展示
     *
     * @param componentName
     * @param childrenName
     */
    private InitializedComponent(ComponentEnum componentName, String childrenName) {
        this.componentName = componentName.getComponentName();
        this.showName = componentName.getShowName();
        this.startMillis = DurationUtils.nowMillis();
        this.children = buildChildren(childrenName, BigDecimal.ZERO);
    }

    public static InitializedComponent root(ComponentEnum componentName) {
        return new InitializedComponent(componentName);
    }

    public static InitializedComponent buildChildTreeItem(ComponentEnum componentName, String childrenName) {
        return new InitializedComponent(componentName, childrenName);
    }

    public void insertChildren(Children children) {
        if (this.children == null) {
            List<Children> childrenList = new LinkedList<>();
            childrenList.add(children);
            this.children = childrenList;
        } else {
            this.children.add(children);
        }
    }

    public List<Children> buildChildren(String childrenName, BigDecimal costTime) {
        if (this.children == null) {
            List<Children> childrenList = new LinkedList<>();
            childrenList.add(new Children(childrenName, costTime));
            return childrenList;
        } else {
            this.children.add(new Children(childrenName, costTime));
            return this.children;
        }
    }

    public InitializedComponent initialized() {
        duration = DurationUtils.nowMillis().subtract(startMillis);
        return this;
    }

    @Data
    @AllArgsConstructor
    public static class Children {

        /**
         * Echarts展示名
         */
        @JSONField(name = "name")
        private String showName;

        /**
         * Echarts数字值
         */
        @JSONField(name = "value")
        private BigDecimal duration;

    }

}
