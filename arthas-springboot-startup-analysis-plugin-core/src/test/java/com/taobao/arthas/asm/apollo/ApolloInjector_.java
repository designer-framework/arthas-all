package com.taobao.arthas.asm.apollo;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.agent.inst.InstrumentApi;

@Instrument(Class = "com.ctrip.framework.apollo.build.ApolloInjector")
public class ApolloInjector_ {

    public static final String DefaultConfigManager = "com.ctrip.framework.apollo.internals.DefaultConfigManager";

    private static final String ConfigManager = "com.ctrip.framework.apollo.internals.ConfigManager";

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(java.lang.Class)
     */
    public static <T> T getInstance(Class<T> clazz) {

        T instance = InstrumentApi.invokeOrigin();

        if (instance instanceof Class && !DefaultConfigManager.equals(((Class<?>) instance).getName())) {
            System.err.println(String.format(((Class<?>) instance).getName()) + " is replaced with " + DefaultConfigManager);
        }

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        if (ConfigManager.equals(clazz.getName())) {
            try {
                return (T) Class.forName(DefaultConfigManager, true, classLoader);
            } catch (Exception e) {
            }
        }

        return instance;
    }

}