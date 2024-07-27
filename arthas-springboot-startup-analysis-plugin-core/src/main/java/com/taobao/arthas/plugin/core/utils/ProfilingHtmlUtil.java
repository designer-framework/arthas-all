package com.taobao.arthas.plugin.core.utils;

import com.taobao.arthas.core.properties.AgentOutputProperties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.function.Function;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-16 23:40
 */
@Slf4j
public class ProfilingHtmlUtil {

    public static final String tailwindJs_ = "/tailwind.js";

    public static final String startupAnalysis_ = "/startup-analysis.html";

    public static final String hyperappJs_ = "/hyperapp.js";

    public static final String flameGraph_ = "/flame-graph.html";

    private final AgentOutputProperties agentOutputProperties;

    public ProfilingHtmlUtil(AgentOutputProperties agentOutputProperties) {
        this.agentOutputProperties = agentOutputProperties;
    }

    public void copyFileToOutputPath(String... fileNames) {
        for (String fileName : fileNames) {
            copyFileToOutputPath(fileName, Function.identity());
        }
    }

    @SneakyThrows
    public void copyFileToOutputPath(String fileName, Function<String, String> resolvePlaceholders) {
        FileUtils.write(getOutputFile(fileName), resolvePlaceholders.apply(resourrceToString(fileName)), StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public File getOutputFile(String fileName) {
        File outputFile = new File(agentOutputProperties.getOutputPath(), fileName);
        if (!outputFile.exists()) {
            FileUtils.touch(outputFile);
        }
        return outputFile;
    }

    @SneakyThrows
    public String readOutputResourrceToString(String fileName) {
        try (InputStream inputStream = Files.newInputStream(new File(agentOutputProperties.getOutputPath(), fileName).toPath())) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

    @SneakyThrows
    public String resourrceToString(String fileName) {
        try (InputStream inputStream = new ClassPathResource(fileName, getClass().getClassLoader()).getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        }
    }

}
