package com.taobao.arthas.spring.extension;

import com.taobao.arthas.profiling.api.processor.ProfilingLifeCycle;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author linyimin
 **/
@Component
public class StacktraceProfiler implements ProfilingLifeCycle {

    private static final LinkedBlockingQueue<StackTraceElement[]> STACK_TRACE_QUEUE = new LinkedBlockingQueue<>();

    private static final Map<String, Integer> TRACE_MAP = new ConcurrentHashMap<>();

    private final ScheduledExecutorService SAMPLE_SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    private List<Thread> sampledThreads = new ArrayList<>();

    @Override
    public void start() {

        AtomicInteger count = new AtomicInteger();
        sampledThreads = getTargetThreads();

        SAMPLE_SCHEDULER.scheduleAtFixedRate(() -> {

            // refresh per second
            if (count.get() % (1000 / 10) == 0) {
                sampledThreads = getTargetThreads();
            }
            count.getAndIncrement();

            for (Thread thread : sampledThreads) {
                addStackTraceElements(thread.getStackTrace());
            }
        }, 0, 10, TimeUnit.MILLISECONDS);

        new Thread(new TraceProcessor()).start();
    }

    @Override
    public void stop() {

        SAMPLE_SCHEDULER.shutdown();
        TraceProcessor.stop();

    }

    private synchronized void addStackTraceElements(StackTraceElement[] elements) {
        STACK_TRACE_QUEUE.add(elements);
    }

    private List<Thread> getTargetThreads() {

        String sampleThreadNames = "main";

        return new ArrayList<>(ThreadUtils.findThreads(thread -> {

            if (StringUtils.isBlank(sampleThreadNames)) {
                return true;
            }

            return Arrays.stream(sampleThreadNames.split(",")).anyMatch(name -> {
                if (name.contains("*")) {
                    return Pattern.compile(name).matcher(thread.getName()).matches();
                } else {
                    return name.equals(thread.getName());
                }
            });
        }));
    }

    static class TraceProcessor implements Runnable {

        private static boolean stop = false;

        public static void stop() {
            stop = true;
        }

        @Override
        public void run() {
            while (true) {

                try {
                    StackTraceElement[] traces = STACK_TRACE_QUEUE.poll(5, TimeUnit.SECONDS);
                    if (traces == null || traces.length == 0) {
                        continue;
                    }

                    List<StackTraceElement> elements = Arrays.asList(traces);

                    if (elements.stream().anyMatch(element -> element.getClassName().startsWith("io.github.linyimin0812.profiler"))) {
                        continue;
                    }

                    Collections.reverse(elements);
                    String trace = elements.stream().map(element -> element.getClassName() + "." + element.getMethodName()).collect(Collectors.joining(";"));
                    TRACE_MAP.put(trace, TRACE_MAP.getOrDefault(trace, 0) + 1);
                } catch (InterruptedException ignored) {
                }

                if (stop && STACK_TRACE_QUEUE.isEmpty()) {
                    break;
                }
            }
        }
    }

}
