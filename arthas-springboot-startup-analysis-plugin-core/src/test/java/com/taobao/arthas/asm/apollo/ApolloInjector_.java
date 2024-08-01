package com.taobao.arthas.asm.apollo;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.agent.inst.InstrumentApi;
import com.taobao.arthas.plugin.core.turbo.constants.TurboConstants;

@Instrument(Class = "com.ctrip.framework.apollo.build.ApolloInjector")
public class ApolloInjector_ {

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(Class)
     */
    public static <T> T getInstance(Class<T> clazz) {

        if (TurboConstants.ConfigManager.equals(clazz.getName())) {

            try {

                System.out.println("3: " + clazz.getName());
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                Class<T> defaultConfigManager = (Class<T>) Class.forName(TurboConstants.DefaultConfigManager, true, classLoader);
                System.err.println("is replaced with " + TurboConstants.DefaultConfigManager);
                System.out.println(4);
                return defaultConfigManager.newInstance();

            } catch (Exception e) {
                //替换失败
                System.out.println(5);
                return InstrumentApi.invokeOrigin();
            }

        } else {

            System.out.println("6: " + clazz.getName());
            return InstrumentApi.invokeOrigin();

        }

    }

}