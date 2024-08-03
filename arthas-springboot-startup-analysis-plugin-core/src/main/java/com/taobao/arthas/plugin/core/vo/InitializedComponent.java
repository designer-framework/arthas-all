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

    private transient boolean lazyRoot;

    private transient ComponentEnum parent;

    /**
     * 组件名
     */
    private ComponentEnum componentName;

    /**
     * Echarts展示名
     */
    @JSONField(name = "name")
    private String name;

    private ConcurrentLinkedDeque<InitializedComponent> children = new ConcurrentLinkedDeque<>();

    private InitializedComponent(ComponentEnum componentName) {
        super();
        this.componentName = componentName;
    }

    public static InitializedComponent root(ComponentEnum componentName, BigDecimal startMillis) {
        InitializedComponent child = new InitializedComponent(componentName);
        child.setName(componentName.getShowName());
        child.setStartMillis(startMillis);
        return root(componentName, startMillis, false);
    }

    public static InitializedComponent root(ComponentEnum componentName, BigDecimal startMillis, boolean lazyRoot) {
        InitializedComponent child = new InitializedComponent(componentName);
        child.setLazyRoot(lazyRoot);
        child.setName(componentName.getShowName());
        child.setStartMillis(startMillis);
        return child;
    }

    public static InitializedComponent child(ComponentEnum parent, String showName, BigDecimal startMillis) {
        InitializedComponent child = new InitializedComponent(parent);
        child.setParent(parent);
        child.setName(showName);
        child.setStartMillis(startMillis);
        return child;
    }

    public void insertChildren(List<InitializedComponent> children) {
        this.children.addAll(children);
    }

    public void updateDurationByChildren() {
        if (!CollectionUtils.isEmpty(children)) {
            setEndMillis(BigDecimal.ZERO);
            setDuration(children.stream().map(InitializedComponent::getDuration).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
    }

}
