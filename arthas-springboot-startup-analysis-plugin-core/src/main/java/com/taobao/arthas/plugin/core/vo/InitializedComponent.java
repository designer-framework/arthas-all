package com.taobao.arthas.plugin.core.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.taobao.arthas.core.vo.DurationVO;
import com.taobao.arthas.plugin.core.enums.ComponentEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
public class InitializedComponent extends DurationVO {

    /**
     * 组件名
     */
    private final String componentName;

    /**
     * Echarts展示名
     */
    @JSONField(name = "name")
    private final String name;

    private List<Children> children = new LinkedList<>();

    private transient volatile Children lastChild;

    private InitializedComponent(ComponentEnum componentName) {
        this.componentName = componentName.getComponentName();
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
        this.children = buildChildren(childrenName, BigDecimal.ZERO);
    }

    public static InitializedComponent root(ComponentEnum componentName) {
        return new InitializedComponent(componentName);
    }

    public static InitializedComponent.Children child(String showName, BigDecimal duration) {
        return new InitializedComponent.Children(showName, duration);
    }

    public void insertChildren(Children children) {
        this.children.add(children);
        this.lastChild = children;
    }

    private List<Children> buildChildren(String childrenName, BigDecimal costTime) {
        if (this.children == null) {
            List<Children> childrenList = new LinkedList<>();
            childrenList.add(new Children(childrenName, costTime));
            return childrenList;
        } else {
            this.children.add(new Children(childrenName, costTime));
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

        /**
         * Echarts展示名
         */
        @JSONField(name = "name")
        private String showName;

        public Children(String showName, BigDecimal duration) {
            super(duration);
            this.showName = showName;
        }

        public Children(String showName) {
            this.showName = showName;
        }

    }

}
