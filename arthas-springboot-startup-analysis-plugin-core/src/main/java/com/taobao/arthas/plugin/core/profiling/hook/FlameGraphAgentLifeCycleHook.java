package com.taobao.arthas.plugin.core.profiling.hook;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.properties.ArthasThreadTraceProperties;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.core.Ordered;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

/**
 * 火焰图
 **/
@Slf4j
public class FlameGraphAgentLifeCycleHook implements AgentLifeCycleHook, Ordered {

    private final LinkedBlockingQueue<StackTraceElement[]> stackTraceQueue = new LinkedBlockingQueue<>();

    private final ScheduledExecutorService SAMPLE_SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 采样次数
     */
    private final AtomicInteger count = new AtomicInteger();

    private final ArthasThreadTraceProperties arthasThreadTraceProperties;

    private final SpringAgentStatisticsVO springAgentStatisticsVO;

    /**
     * 被采样的线程
     */
    private List<Thread> sampledThreads = new ArrayList<>();
    private volatile boolean stop = false;

    public FlameGraphAgentLifeCycleHook(ArthasThreadTraceProperties arthasThreadTraceProperties, SpringAgentStatisticsVO springAgentStatisticsVO) {
        this.arthasThreadTraceProperties = arthasThreadTraceProperties;
        this.springAgentStatisticsVO = springAgentStatisticsVO;
    }

    @Override
    public void start() {

        //寻找main线程
        sampledThreads = getTargetThreads();

        //每10ms刷新一次main线程
        SAMPLE_SCHEDULER.scheduleAtFixedRate(() -> {

            // refresh per second
            if (count.get() % (1000 / arthasThreadTraceProperties.getInterval()) == 0) {
                sampledThreads = getTargetThreads();
            }
            count.getAndIncrement();

            for (Thread thread : sampledThreads) {
                addStackTraceElements(thread.getStackTrace());
            }
        }, 0, arthasThreadTraceProperties.getInterval(), TimeUnit.MILLISECONDS);

        new Thread(this::collectStackTrace).start();
    }

    private void collectStackTrace() {
        while (true) {

            try {
                StackTraceElement[] traces = stackTraceQueue.poll(5, TimeUnit.SECONDS);
                if (traces == null || traces.length == 0) {
                    continue;
                }

                springAgentStatisticsVO.addInvokeTrace(traces);

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
        return LifeCycleOrdered.STOP_FLAME_GRAPH_PROFILER;
    }

    private synchronized void addStackTraceElements(StackTraceElement[] elements) {
        stackTraceQueue.add(elements);
    }

    private List<Thread> getTargetThreads() {

        return new ArrayList<>(ThreadUtils.findThreads(thread -> {

            if (CollectionUtils.isEmpty(arthasThreadTraceProperties.getNames())) {
                return true;
            }

            return arthasThreadTraceProperties.getNames().stream()
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
