package com.taobao.arthas.core.utils;

import com.taobao.arthas.common.AnsiLog;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.security.CodeSource;

/**
 * @description:
 * @author: Designer
 * @date : 2024-07-16 23:40
 */
@Slf4j
public class AgentHomeUtil {

    @SneakyThrows
    public static File getOutputFile(String fileName) {
        File outputFile = new File(getOutputPath(fileName));
        if (!outputFile.exists()) {
            FileUtils.touch(outputFile);
        }
        return outputFile;
    }

    public static String getOutputPath(String fileName) {
        return AgentHomeUtil.arthasHome() + File.separator + "output" + File.separator + fileName;
    }

    public static String arthasHome() {
        CodeSource codeSource = AgentHomeUtil.class.getProtectionDomain().getCodeSource();
        if (codeSource != null) {
            try {
                return new File(codeSource.getLocation().toURI().getSchemeSpecificPart()).getParentFile().getAbsolutePath();
            } catch (Throwable e) {
                AnsiLog.error("try to find arthas.home from CodeSource error", e);
            }
        }
        return new File("").getAbsolutePath();
    }

}
