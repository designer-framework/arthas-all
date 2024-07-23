package com.taobao.arthas.core.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-16 23:40
 */
@Slf4j
@Component
public class ProfilingHtmlUtil {

    public static final String tailwindJs_ = "/tailwind.js";

    public static final String startupAnalysis_ = "/startup-analysis.html";

    public static final String hyperappJs_ = "/hyperapp.js";

    public static final String flameGraph_ = "/flame-graph.html";

    @Value("classpath:" + tailwindJs_)
    private Resource tailwindJs;

    @Value("classpath:" + startupAnalysis_)
    private Resource startupAnalysis;

    @Value("classpath:" + hyperappJs_)
    private Resource hyperappJs;

    @Value("classpath:" + flameGraph_)
    private Resource flameGraph;

    @SneakyThrows
    private String getStartupAnalysisHtml() {
        try (InputStream inputStream = startupAnalysis.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    private String getTailwindJs() {
        try (InputStream inputStream = tailwindJs.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    private String getHyperAppJs() {
        try (InputStream inputStream = hyperappJs.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    private String getFlameGraphHtml() {
        try (InputStream inputStream = flameGraph.getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    public void writeHtml(String fileName, Consumer<String> consumer) {
        consumer.accept(getHtml(fileName));
    }

    public void writeHtml(String fileName) {
        writeHtml(fileName, Function.identity());
    }

    @SneakyThrows
    public void writeHtml(String fileName, Function<String, String> replaceFun) {
        FileUtils.write(AgentHomeUtil.getOutputFile(fileName), replaceFun.apply(getHtml(fileName)), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public void writeResultHtml(String fileName, Consumer<String> startupAnalysisResult) {
        try (InputStream inputStream = FileUtils.openInputStream(AgentHomeUtil.getOutputFile(fileName))) {
            startupAnalysisResult.accept(IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
    }

    private String getHtml(String fileName) {
        switch (fileName) {
            case startupAnalysis_:
                return getStartupAnalysisHtml();
            case hyperappJs_:
                return getHyperAppJs();
            case tailwindJs_:
                return getTailwindJs();
            case flameGraph_:
                return getFlameGraphHtml();
            default:
                return "";
        }
    }

}
