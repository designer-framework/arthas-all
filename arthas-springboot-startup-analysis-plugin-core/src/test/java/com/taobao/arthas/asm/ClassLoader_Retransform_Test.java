package com.taobao.arthas.asm;

import com.alibaba.bytekit.asm.instrument.InstrumentParseResult;
import com.alibaba.bytekit.asm.instrument.InstrumentTemplate;
import com.alibaba.bytekit.asm.instrument.InstrumentTransformer;
import com.alibaba.bytekit.utils.IOUtils;
import com.ctrip.framework.apollo.Config;
import com.taobao.arthas.asm.apollo.ApolloInjector_;
import com.taobao.arthas.asm.apollo.DefaultConfigManager_;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.junit.Test;

import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author hengyunabc 2020-11-30
 */
public class ClassLoader_Retransform_Test {

    public static final String ApolloInjector = "com.ctrip.framework.apollo.build.ApolloInjector";

    public static final String ConfigManager = "com.ctrip.framework.apollo.internals.DefaultConfigManager";

    public static void main(String[] args) {
        List<CompletableFuture<Void>> collect = IntStream.range(0, 10).mapToObj(operand -> {

            return CompletableFuture.runAsync(() -> {
                System.out.println("In: " + operand);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Out: " + operand);
            });

        }).collect(Collectors.toList());

        collect.forEach(voidCompletableFuture -> {
            try {
                voidCompletableFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

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
        InstrumentTransformer instrumentTransformer;

        instrumentTransformer = new InstrumentTransformer(instrumentParseResult);
        instrumentation.addTransformer(instrumentTransformer, true);

        instrumentation.retransformClasses(cl.loadClass(ApolloInjector), cl.loadClass(ConfigManager));
        instrumentation.removeTransformer(instrumentTransformer);

        com.ctrip.framework.apollo.internals.ConfigManager instance = com.ctrip.framework.apollo.build.ApolloInjector.getInstance(com.ctrip.framework.apollo.internals.ConfigManager.class);
        Config config = instance.getConfig("B");

        System.out.println(123);
    }

    @Test
    public void test1() {

    }


}
