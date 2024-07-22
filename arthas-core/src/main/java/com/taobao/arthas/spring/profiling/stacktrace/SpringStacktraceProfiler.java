package com.taobao.arthas.spring.profiling.stacktrace;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import com.taobao.arthas.spring.constants.ProfilingLifeCycleOrdered;
import com.taobao.arthas.spring.properties.ThreadProfilingProperties;
import com.taobao.arthas.spring.vo.ProfilingResultVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 火焰图
 **/
@Slf4j
@Component
public class SpringStacktraceProfiler implements ProfilingLifeCycle, Ordered {

    private final LinkedBlockingQueue<StackTraceElement[]> stackTraceQueue = new LinkedBlockingQueue<>();

    private final ScheduledExecutorService SAMPLE_SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 采样次数
     */
    private final AtomicInteger count = new AtomicInteger();

    /**
     * 被采样的线程
     */
    private List<Thread> sampledThreads = new ArrayList<>();

    @Autowired
    private ThreadProfilingProperties threadProfilingProperties;

    @Autowired
    private ProfilingResultVO profilingResultVO;

    private volatile boolean stop = false;

    @Override
    public void start() {

        //寻找main线程
        sampledThreads = getTargetThreads();

        //每10ms刷新一次main线程
        SAMPLE_SCHEDULER.scheduleAtFixedRate(() -> {

            // refresh per second
            if (count.get() % (1000 / threadProfilingProperties.getInterval()) == 0) {
                sampledThreads = getTargetThreads();
            }
            count.getAndIncrement();

            for (Thread thread : sampledThreads) {
                addStackTraceElements(thread.getStackTrace());
            }
        }, 0, threadProfilingProperties.getInterval(), TimeUnit.MILLISECONDS);

        new Thread(this::collectTrace).start();
    }

    private void collectTrace() {

        while (true) {

            try {
                StackTraceElement[] traces = stackTraceQueue.poll(5, TimeUnit.SECONDS);
                if (traces == null || traces.length == 0) {
                    continue;
                }

                List<StackTraceElement> elements = Arrays.asList(traces);

                if (elements.stream().anyMatch(element -> element.getClassName().startsWith("com.taobao.arthas"))) {
                    continue;
                }

                Collections.reverse(elements);
                String trace = elements.stream().map(element -> element.getClassName() + "." + element.getMethodName()).collect(Collectors.joining(";"));

                profilingResultVO.addInvokeTrace(trace);

            } catch (InterruptedException ignored) {
            }

            if (stop && stackTraceQueue.isEmpty()) {
                break;
            }

        }

    }

    @Override
    public void stop() {

        SAMPLE_SCHEDULER.shutdown();
        stop = true;

    }

    @Override
    public int getOrder() {
        return ProfilingLifeCycleOrdered.STOP_STACK_TRACE_PROFILER;
    }

    private synchronized void addStackTraceElements(StackTraceElement[] elements) {
        stackTraceQueue.add(elements);
    }

    private List<Thread> getTargetThreads() {

        return new ArrayList<>(ThreadUtils.findThreads(thread -> {

            if (CollectionUtils.isEmpty(threadProfilingProperties.getNames())) {
                return true;
            }

            return threadProfilingProperties.getNames().stream()
                    .anyMatch(name -> {
                        if (name.contains("*")) {
                            return Pattern.compile(name).matcher(thread.getName()).matches();
                        } else {
                            return name.equals(thread.getName());
                        }
                    });
        }));
    }

}
