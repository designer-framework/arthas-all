package com.taobao.arthas.plugin.core.turbo.instrument;

import com.alibaba.bytekit.agent.inst.Instrument;
import com.alibaba.bytekit.agent.inst.InstrumentApi;
import com.taobao.arthas.plugin.core.turbo.constants.TurboConstants;

@Instrument(Class = TurboConstants.ApolloInjector)
public class ApolloInjector {

    /**
     * @param clazz
     * @param <T>
     * @return
     * @see com.ctrip.framework.apollo.build.ApolloInjector#getInstance(Class)
     */
    public static <T> T getInstance(Class<T> clazz) {
        T instance = InstrumentApi.invokeOrigin();

        if (instance instanceof Class && TurboConstants.ConfigManager.equals(clazz.getName())) {

            if (TurboConstants.DefaultConfigManager.equals(((Class<?>) instance).getName())) {

                return instance;

                //不是默认实现, 则改为默认实现
            } else {

                try {

                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                    T defaultConfigManager = (T) Class.forName(TurboConstants.DefaultConfigManager, true, classLoader);
                    System.err.println(String.format(((Class<?>) instance).getName()) + " is replaced with " + TurboConstants.DefaultConfigManager);
                    return defaultConfigManager;

                } catch (Exception e) {
                    //替换失败
                    return instance;
                }

            }

        } else {

            return instance;

        }

    }

}