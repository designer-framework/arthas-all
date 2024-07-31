package com.taobao.arthas.plugin.core.profiling.component;

import com.taobao.arthas.api.vo.ClassMethodInfo;
import com.taobao.arthas.api.vo.InvokeVO;
import com.taobao.arthas.plugin.core.enums.SpringComponentEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * 只是为了统计加载了哪些名命空间
 */
@Slf4j
public class ClassPathScanningCandidateComponentPointcutAdvisor extends AbstractComponentCreatorPointcutAdvisor {

    /**
     * @return
     * @see org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider#findCandidateComponents(java.lang.String)
     */
    public ClassPathScanningCandidateComponentPointcutAdvisor(SpringComponentEnum springComponentEnum, ClassMethodInfo classMethodInfo) {
        super(springComponentEnum, classMethodInfo);
    }

    @Override
    protected String childItemName(InvokeVO invokeVO) {
        return String.valueOf(invokeVO.getParams()[0]);
    }

}
