package com.taobao.arthas.common;

import java.util.logging.Level;
import java.util.regex.Matcher;

/**
 * <pre>
 * FINEST  -> TRACE
 * FINER   -> DEBUG
 * FINE    -> DEBUG
 * CONFIG  -> INFO
 * INFO    -> INFO
 * WARNING -> WARN
 * SEVERE  -> ERROR
 * </pre>
 *
 * @author hengyunabc 2017-05-03
 * @see org.slf4j.bridge.SLF4JBridgeHandler
 */
public abstract class AnsiLog {

    private static final String RESET = "\033[0m";
    
    private static final String DEBUG_PREFIX = "[DEBUG] ";

    private static final String INFO_PREFIX = "[INFO] ";

    private static final String WARN_PREFIX = "[WARN] ";

    private static final String ERROR_PREFIX = "[ERROR] ";

    public static java.util.logging.Level LEVEL = java.util.logging.Level.CONFIG;

    private AnsiLog() {
    }

    /**
     * set logger Level
     *
     * @param level
     * @return
     * @see java.util.logging.Level
     */
    public static Level level(Level level) {
        Level old = LEVEL;
        LEVEL = level;
        return old;
    }

    /**
     * get current logger Level
     *
     * @return
     */
    public static Level level() {
        return LEVEL;
    }

    private static String colorStr(String msg, int colorCode) {
        return "\033[" + colorCode + "m" + msg + RESET;
    }

    public static void debug(String msg) {
        if (canLog(Level.FINER)) {
            System.out.println(DEBUG_PREFIX + msg);
        }
    }

    public static void debug(String format, Object... arguments) {
        if (canLog(Level.FINER)) {
            debug(format(format, arguments));
        }
    }

    public static void debug(Throwable t) {
        if (canLog(Level.FINER)) {
            t.printStackTrace(System.out);
        }
    }

    public static void info(String msg) {
        if (canLog(Level.CONFIG)) {
            System.out.println(INFO_PREFIX + msg);
        }
    }

    public static void info(String format, Object... arguments) {
        if (canLog(Level.CONFIG)) {
            info(format(format, arguments));
        }
    }

    public static void info(Throwable t) {
        if (canLog(Level.CONFIG)) {
            t.printStackTrace(System.out);
        }
    }

    public static void warn(String msg) {
        if (canLog(Level.WARNING)) {
            System.out.println(WARN_PREFIX + msg);
        }
    }

    public static void warn(String format, Object... arguments) {
        if (canLog(Level.WARNING)) {
            warn(format(format, arguments));
        }
    }

    public static void warn(Throwable t) {
        if (canLog(Level.WARNING)) {
            t.printStackTrace(System.out);
        }
    }

    public static void error(String msg) {
        if (canLog(Level.SEVERE)) {
            System.out.println(ERROR_PREFIX + msg);
        }
    }

    public static void error(String format, Object... arguments) {
        if (canLog(Level.SEVERE)) {
            error(format(format, arguments));
        }
    }

    public static void error(Throwable t) {
        if (canLog(Level.SEVERE)) {
            t.printStackTrace(System.out);
        }
    }

    private static String format(String from, Object... arguments) {
        if (from != null) {
            String computed = from;
            if (arguments != null && arguments.length != 0) {
                for (Object argument : arguments) {
                    computed = computed.replaceFirst("\\{\\}", Matcher.quoteReplacement(String.valueOf(argument)));
                }
            }
            return computed;
        }
        return null;
    }

    private static boolean canLog(Level level) {
        return level.intValue() >= LEVEL.intValue();
    }

}
