package com.taobao.arthas.asm;

import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTemplate;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.utils.IOUtils;
import com.taobao.arthas.asm.apollo.ApolloInjector_;
import com.taobao.arthas.asm.apollo.DefaultConfigManager_;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import java.lang.instrument.Instrumentation;

/**
 * @author hengyunabc 2020-11-30
 */
public class ClassLoader_Retransform_Test {
    public static final String ApolloInjector = "com.ctrip.framework.apollo.build.ApolloInjector";

    public static final String ConfigManager = "com.ctrip.framework.apollo.internals.DefaultConfigManager";

    private static final String NewDefaultConfigManager = "com.taobao.arthas.asm.apollo.DefaultConfigManager";

    @Test
    public void test() throws Exception {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Instrumentation instrumentation = ByteBuddyAgent.install();

        InstrumentTemplate template = new InstrumentTemplate();

        byte[] classBytes = IOUtils.getBytes(this.getClass().getClassLoader().getResourceAsStream(ApolloInjector_.class.getName().replace('.', '/') + ".class"));
        template.addInstrumentClass(classBytes);

        byte[] classBytes_ = IOUtils.getBytes(this.getClass().getClassLoader().getResourceAsStream(DefaultConfigManager_.class.getName().replace('.', '/') + ".class"));
        template.addInstrumentClass(classBytes_);

        InstrumentParseResult instrumentParseResult = template.build();
        InstrumentTransformer instrumentTransformer = null;
        try {

            instrumentTransformer = new InstrumentTransformer(instrumentParseResult);
            instrumentation.addTransformer(instrumentTransformer, true);

            instrumentation.retransformClasses(cl.loadClass(ConfigManager));
            instrumentation.retransformClasses(cl.loadClass(ApolloInjector));

        } finally {

            instrumentation.removeTransformer(instrumentTransformer);

        }
    }

    @Test
    public void test1() {

    }


}
