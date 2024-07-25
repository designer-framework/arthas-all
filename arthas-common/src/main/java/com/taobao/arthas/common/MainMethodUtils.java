package com.taobao.arthas.common;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

public class MainMethodUtils {

    public static String[] inputArguments() {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        return bean.getInputArguments().toArray(new String[0]);
    }

}
