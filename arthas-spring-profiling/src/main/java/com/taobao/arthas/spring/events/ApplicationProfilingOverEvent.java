package com.taobao.arthas.spring.events;

public class ApplicationProfilingOverEvent extends ProfilingEvent {

    private long startTime;

    private long startUpTime;

    public ApplicationProfilingOverEvent(Object source, long startTime) {
        super(source);
        startUpTime = getStopTime() - startTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getStopTime() {
        return getTimestamp();
    }

    /**
     * 启动耗时
     *
     * @return
     */
    public long startUpTime() {
        return startUpTime;
    }

}
