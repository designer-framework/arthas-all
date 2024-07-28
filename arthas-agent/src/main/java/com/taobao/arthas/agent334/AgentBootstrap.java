package com.taobao.arthas.agent334;

import com.taobao.arthas.agent.ArthasClassloader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 代理启动类
 *
 * @author vlinux on 15/5/19.
 */
public class AgentBootstrap {

    private static final String ARTHAS_CORE_JAR = "arthas-core.jar";

    private static final String ARTHAS_AGENT_JAR = "arthas-agent.jar";

    private static final String ARTHAS_BOOTSTRAP = "com.taobao.arthas.core.ArthasBootstrap";

    private static final String GET_INSTANCE = "getInstance";

    private static PrintStream ps = System.err;

    /**
     * <pre>
     * 1. 全局持有classloader用于隔离 Arthas 实现，防止多次attach重复初始化
     * 2. ClassLoader在arthas停止时会被reset
     * 3. 如果ClassLoader一直没变，则 com.taobao.arthas.core.server.ArthasBootstrap#getInstance 返回结果一直是一样的
     * </pre>
     */
    private static volatile ClassLoader arthasClassLoader;

    static {

        try {

            File arthasLogDir = new File(System.getProperty("user.home") + File.separator + "logs" + File.separator + "arthas" + File.separator);
            if (!arthasLogDir.exists()) {
                arthasLogDir.mkdirs();
            }

            if (!arthasLogDir.exists()) {
                // #572
                arthasLogDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "logs" + File.separator + "arthas" + File.separator);
                if (!arthasLogDir.exists()) {
                    arthasLogDir.mkdirs();
                }
            }

            File log = new File(arthasLogDir, "arthas.log");

            if (!log.exists()) {
                log.createNewFile();
            }

            ps = new PrintStream(new FileOutputStream(log, true));

        } catch (Throwable t) {
            t.printStackTrace(ps);
        }

    }

    public static void premain(String args, Instrumentation inst) {
        main(args, inst);
    }

    public static void agentmain(String args, Instrumentation inst) {
        main(args, inst);
    }

    /**
     * 让下次再次启动时有机会重新加载
     */
    public static void resetArthasClassLoader() {
        arthasClassLoader = null;
    }

    private static ClassLoader getClassLoader(Instrumentation inst, File arthasCoreJarFile) throws Throwable {
        // 构造自定义的类加载器，尽量减少Arthas对现有工程的侵蚀
        return loadOrDefineClassLoader(arthasCoreJarFile);
    }

    private static ClassLoader loadOrDefineClassLoader(File arthasCoreJarFile) throws Throwable {
        if (arthasClassLoader == null) {
            arthasClassLoader = new ArthasClassloader(getAllJarFileURL(arthasCoreJarFile));
        }
        return arthasClassLoader;
    }

    /**
     * 加载获取agent同路径下所有jar包以及plugins子文件夹路径下所有的jar包
     *
     * @param arthasCoreJarFile
     * @return
     * @throws Throwable
     */
    private static URL[] getAllJarFileURL(File arthasCoreJarFile) throws Throwable {
        List<URL> jarUrls = new ArrayList<>();
        //core包放在第一位
        jarUrls.add(arthasCoreJarFile.toURI().toURL());
        jarUrls.addAll(findJarFileURL(arthasCoreJarFile.getParentFile()));
        jarUrls.addAll(findJarFileURL(new File(arthasCoreJarFile.getParentFile(), "plugins")));
        return jarUrls.toArray(new URL[]{});
    }

    private static List<URL> findJarFileURL(File file) throws Throwable {
        if (file.exists() && file.isDirectory()) {

            List<URL> jarUrls = new ArrayList<>();

            File[] plugins = file.listFiles();
            for (File plugin : plugins) {
                String jarFileName = plugin.getName();
                if (!ARTHAS_AGENT_JAR.equals(jarFileName) && !ARTHAS_CORE_JAR.equals(jarFileName)) {
                    jarUrls.add(plugin.toURI().toURL());
                }
            }

            return jarUrls;

        } else {
            return new ArrayList<>();
        }
    }

    private static synchronized void main(String args, Instrumentation inst) {
        try {

            ps.println("Arthas server agent start...");

            // 传递的args参数分两个部分:arthasCoreJar路径和agentArgs, 分别是Agent的JAR包路径和期望传递到服务端的参数
            if (args == null) {
                args = "";
            }
            args = decodeArg(args);

            String arthasCoreJar;
            String agentArgs;
            int index = args.indexOf(';');
            if (index != -1) {
                arthasCoreJar = args.substring(0, index);
                agentArgs = args.substring(index);
            } else {
                arthasCoreJar = "";
                agentArgs = args;
            }

            File arthasCoreJarFile = new File(arthasCoreJar);
            if (!arthasCoreJarFile.exists()) {

                ps.println("Can not find arthas-core jar file from args: " + arthasCoreJarFile);
                // try to find from arthas-agent.jar directory
                CodeSource codeSource = AgentBootstrap.class.getProtectionDomain().getCodeSource();
                if (codeSource != null) {

                    try {

                        File arthasAgentJarFile = new File(getAgentLibPath());
                        arthasCoreJarFile = new File(arthasAgentJarFile, ARTHAS_CORE_JAR);
                        if (!arthasCoreJarFile.exists()) {
                            ps.println("Can not find arthas-core jar file from agent jar directory: " + arthasAgentJarFile);
                        }

                    } catch (Throwable e) {
                        ps.println("Can not find arthas-core jar file from " + codeSource.getLocation());
                        e.printStackTrace(ps);
                    }

                }

            }
            if (!arthasCoreJarFile.exists()) {
                return;
            }

            /**
             * Use a dedicated thread to run the binding logic to prevent possible memory leak. #195
             */
            ClassLoader agentLoader = getClassLoader(inst, arthasCoreJarFile);

            //新开线程, 与原APP应用的类加载器完全隔离
            Thread bindingThread = new Thread(() -> {
                try {

                    Thread.currentThread().setContextClassLoader(agentLoader);
                    bind(inst, agentLoader, agentArgs);

                } catch (Throwable throwable) {
                    throwable.printStackTrace(ps);
                }
            });

            bindingThread.setName("agent-thread");
            bindingThread.start();
            bindingThread.join();

        } catch (Throwable t) {
            t.printStackTrace(ps);
            try {
                if (ps != System.err) {
                    ps.close();
                }
            } catch (Throwable tt) {
                // ignore
            }
            throw new RuntimeException(t);
        }

    }

    private static void bind(Instrumentation inst, ClassLoader agentLoader, String args) throws Throwable {
        /**
         * <pre>
         * ArthasBootstrap bootstrap = ArthasBootstrap.getInstance(inst);
         * </pre>
         */
        Class<?> bootstrapClass = agentLoader.loadClass(ARTHAS_BOOTSTRAP);
        Object bootstrap = bootstrapClass.getMethod(GET_INSTANCE, Instrumentation.class, String.class).invoke(null, inst, args);

        ps.println("Arthas server already bind.");
    }

    private static String decodeArg(String arg) {
        try {
            return URLDecoder.decode(arg, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return arg;
        }
    }

    private static String getAgentLibPath() throws URISyntaxException {
        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = bean.getInputArguments();

        for (String jvmArg : jvmArgs) {

            int index = jvmArg.indexOf("-javaagent:");
            if (index == -1 || index + 1 >= jvmArg.length()) {
                continue;
            }

            String javaAgentValue = jvmArg.substring(index + 11);

            int i = javaAgentValue.indexOf("=");
            if (i > -1) {

                String agentJarPath = javaAgentValue.substring(0, i);
                if (agentJarPath.endsWith(ARTHAS_AGENT_JAR)) {
                    //Agent所在文件夹
                    return javaAgentValue.substring(0, javaAgentValue.lastIndexOf(File.separator) + 1);
                }

            }

        }

        File file = new File(AgentBootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI().getSchemeSpecificPart());
        if (file.exists()) {
            return file.getParentFile().getAbsolutePath();
        } else {
            throw new IllegalArgumentException("Agent Path Not Found: " + jvmArgs);
        }

    }

}
