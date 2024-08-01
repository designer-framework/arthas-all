package com.taobao.arthas.core.hook;

import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTemplate;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.utils.IOUtils;
import com.taobao.arthas.core.configuration.instrument.RetransformAttribute;
import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.PriorityOrdered;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class InstrumentLifeCycleHook implements AgentLifeCycleHook, BeanClassLoaderAware, PriorityOrdered {

    private final List<RetransformAttribute> retransformAttributes;

    @Setter
    private ClassLoader beanClassLoader;

    @Setter
    private Instrumentation instrumentation;

    public InstrumentLifeCycleHook(List<RetransformAttribute> retransformAttributes) {
        this.retransformAttributes = retransformAttributes;
    }

    @Override
    public void start() {
        //此时的类加载器是AppClassLoader或Spring类加载器
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        InstrumentTemplate template = new InstrumentTemplate();

        try {

            List<String> retransformClasses = new ArrayList<>();
            for (RetransformAttribute retransformAttribute : retransformAttributes) {
                try {
                    byte[] classBytes = IOUtils.getBytes(
                            beanClassLoader.getResourceAsStream(retransformAttribute.getInstrumentClass().getName().replace('.', '/') + ".class")
                    );
                    template.addInstrumentClass(classBytes);
                    retransformClasses.add(retransformAttribute.getClassName());
                } catch (Exception e) {
                    log.error("Enhancement failed: {} <--> {}", retransformAttribute.getClassName(), retransformAttribute.getInstrumentClass());
                }
            }

            InstrumentParseResult instrumentParseResult = template.build();
            InstrumentTransformer instrumentTransformer = null;
            try {

                instrumentTransformer = new InstrumentTransformer(instrumentParseResult);
                instrumentation.addTransformer(instrumentTransformer, true);

                for (String retransformClass : retransformClasses) {
                    log.info("RetransformClass success: {}", retransformClass);
                    instrumentation.retransformClasses(cl.loadClass(retransformClass));
                }

            } finally {

                instrumentation.removeTransformer(instrumentTransformer);

            }

        } catch (Exception e) {
            log.error("Enhancement failed", e);
        }

    }

    @Override
    public int getOrder() {
        return LifeCycleOrdered.AGENT_RETRANSFORM;
    }

}