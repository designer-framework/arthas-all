package com.taobao.arthas.spring.extension;

import com.taobao.arthas.common.OSUtils;
import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import one.profiler.AsyncProfiler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-16 23:14
 */
@Component
public class AsyncProfilingLifeCycle implements ProfilingLifeCycle {

    private ProfilingLifeCycle delegate;

    @Override
    public void start() {
        if (OSUtils.isWindows()) {
            delegate = new ProfilingLifeCycle() {
                @Override
                public void start() {
                    new StacktraceProfiler();
                }

                @Override
                public void stop() {
                }
            };
        } else {
            delegate = new ProfilingLifeCycle() {
                @Override
                public void start() {
                    try {
                        AsyncProfiler.getInstance(getProfilerSoPath()).execute("");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                @Override
                public void stop() {
                    try {
                        AsyncProfiler.getInstance(getProfilerSoPath()).execute("");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        }
    }

    @Override
    public void stop() {
    }

    private String getProfilerSoPath() {
        if (OSUtils.isMac()) {

            // FAT_BINARY support both x86_64/arm64
            return "async-profiler/libasyncProfiler-mac.so";

        } else if (OSUtils.isLinux()) {

            if (OSUtils.isX86_64() && OSUtils.isMuslLibc()) {
                return "async-profiler/libasyncProfiler-linux-musl-x64.so";
            } else if (OSUtils.isX86_64()) {
                return "async-profiler/libasyncProfiler-linux-x64.so";
            } else if (OSUtils.isArm64() && OSUtils.isMuslLibc()) {
                return "async-profiler/libasyncProfiler-linux-musl-arm64.so";
            } else if (OSUtils.isArm64()) {
                return "async-profiler/libasyncProfiler-linux-arm64.so";
            }

        }

        return null;
    }

}
