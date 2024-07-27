package com.taobao.arthas.plugin.core.profiling.hook;

import com.taobao.arthas.core.constants.LifeCycleOrdered;
import com.taobao.arthas.core.lifecycle.AgentLifeCycleHook;
import com.taobao.arthas.core.properties.AgentFlameGraphProperties;
import com.taobao.arthas.plugin.core.vo.SpringAgentStatisticsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.springframework.core.Ordered;
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
public class FlameGraphAgentLifeCycleHook implements AgentLifeCycleHook, Ordered {

    private final LinkedBlockingQueue<StackTraceElement[]> stackTraceQueue = new LinkedBlockingQueue<>();

    private final ScheduledExecutorService SAMPLE_SCHEDULER = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors());

    /**
     * 采样次数
     */
    private final AtomicInteger count = new AtomicInteger();

    private final AgentFlameGraphProperties agentFlameGraphProperties;

    private final SpringAgentStatisticsVO springAgentStatisticsVO;

    /**
     * 被采样的线程
     */
    private List<Thread> sampledThreads = new ArrayList<>();

    private volatile boolean stop = false;

    public FlameGraphAgentLifeCycleHook(AgentFlameGraphProperties agentFlameGraphProperties, SpringAgentStatisticsVO springAgentStatisticsVO) {
        this.agentFlameGraphProperties = agentFlameGraphProperties;
        this.springAgentStatisticsVO = springAgentStatisticsVO;
    }

    @Override
    public void start() {

        //寻找main线程
        sampledThreads = getTargetThreads();

        //每10ms刷新一次main线程
        SAMPLE_SCHEDULER.scheduleAtFixedRate(() -> {

            // refresh per second
            if (count.get() % (1000 / agentFlameGraphProperties.getInterval()) == 0) {
                sampledThreads = getTargetThreads();
            }

            count.getAndIncrement();

            for (Thread thread : sampledThreads) {
                addStackTraceElements(thread.getStackTrace());
            }

        }, 0, agentFlameGraphProperties.getInterval(), TimeUnit.MILLISECONDS);

        new Thread(this::collectStackTrace).start();
    }

    private void collectStackTrace() {
        while (true) {

            try {
                //拉取间隔越短, 采样精度越高
                StackTraceElement[] stackTraceElements = stackTraceQueue.poll(5, TimeUnit.SECONDS);
                if (stackTraceElements == null || stackTraceElements.length == 0) {
                    continue;
                }

                List<StackTraceElement> stackTraceElementList = Arrays.asList(stackTraceElements);

                Collections.reverse(stackTraceElementList);

                //高精度, 不丢弃栈帧
                if (agentFlameGraphProperties.isHighPrecision()) {

                    //将栈帧转换成String, 便于JVM对栈帧的回收
                    springAgentStatisticsVO.addInvokeTrace(
                            stackTraceElementList.stream()
                                    .map(stackTraceElement -> stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName() + ";")
                                    .collect(Collectors.joining())
                    );

                    //默认会将
                } else {

                    StringBuilder stackTraceElementsBuilder = new StringBuilder();
                    label:
                    for (StackTraceElement stackTraceElement : stackTraceElementList) {

                        for (String skipStackTrace : agentFlameGraphProperties.getSkipTrace()) {
                            String className = stackTraceElement.getClassName();
                            //丢弃后续栈帧
                            if (className.startsWith(skipStackTrace)) {
                                break label;
                            }
                        }
                        stackTraceElementsBuilder.append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(";");

                    }
                    //将栈帧转换成String, 便于JVM对栈帧的回收
                    springAgentStatisticsVO.addInvokeTrace(stackTraceElementsBuilder.toString());

                }


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

            if (CollectionUtils.isEmpty(agentFlameGraphProperties.getNames())) {
                return true;
            }

            return agentFlameGraphProperties.getNames().stream()
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
