package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.DurationVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

@Getter
@Setter
public class InitializedComponent extends DurationVO {

    /**
     * 组件名
     */
    private final ComponentEnum componentName;

    /**
     * Echarts展示名
     */
    @JSONField(name = "name")
    private final String name;

    private ConcurrentLinkedDeque<Children> children = new ConcurrentLinkedDeque<>();


    private transient ComponentEnum parent;

    private InitializedComponent(ComponentEnum componentName) {
        this.componentName = componentName;
        this.name = componentName.getShowName();
    }

    /**
     * 两层结构, 便于Echarts展示
     *
     * @param componentName
     * @param childrenName
     */
    private InitializedComponent(ComponentEnum componentName, String childrenName) {
        this(componentName);
        this.children = buildChildren(componentName, childrenName, BigDecimal.ZERO);
    }

    public static InitializedComponent root(ComponentEnum componentName) {
        return new InitializedComponent(componentName);
    }

    public static InitializedComponent.Children child(ComponentEnum parent, String showName, BigDecimal duration) {
        return new InitializedComponent.Children(parent, showName, duration);
    }

    public void insertChildren(Children children) {
        this.children.add(children);
    }

    public void insertChildren(List<Children> children) {
        this.children.addAll(children);
    }

    private ConcurrentLinkedDeque<Children> buildChildren(ComponentEnum parent, String childrenName, BigDecimal costTime) {
        if (this.children == null) {
            ConcurrentLinkedDeque<Children> childrenList = new ConcurrentLinkedDeque<>();
            childrenList.add(new Children(parent, childrenName, costTime));
            return childrenList;
        } else {
            this.children.add(new Children(parent, childrenName, costTime));
            return this.children;
        }
    }

    public InitializedComponent updateDurationByChildren() {
        if (!CollectionUtils.isEmpty(children)) {
            //setEndMillis(children.get(children.size() - 1).getEndMillis());
            setDuration(children.stream().map(Children::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return this;
    }

    @Getter
    @Setter
    public static class Children extends DurationVO {

        private ComponentEnum parent;

        /**
         * Echarts展示名
         */
        @JSONField(name = "name")
        private String showName;

        public Children(ComponentEnum parent, String showName, BigDecimal duration) {
            super(duration);
            this.parent = parent;
            this.showName = showName;
        }

        public Children(String showName) {
            this.showName = showName;
        }

    }

}
