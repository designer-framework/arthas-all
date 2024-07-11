package com.taobao.arthas.spring.hook;

import com.taobao.arthas.profiling.api.hook.ProfilingHook;
import org.springframework.stereotype.Component;

@Component
public class DefaultProfilingHook implements ProfilingHook {

    @Override
    public void hook() {
    }

}
