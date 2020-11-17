package pl.greenmc.tob.game.util;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import static pl.greenmc.tob.game.util.Utilities.*;

/**
 * Main logger
 */
@SuppressWarnings("unused")
public class Logger {
    /**
     * Black text
     */
    public static final String ANSI_BLACK = "\u001B[30m";
    /**
     * Black background
     */
    public static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
    /**
     * Blue text
     */
    public static final String ANSI_BLUE = "\u001B[34m";
    /**
     * Blue background
     */
    public static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
    /**
     * Cyan text
     */
    public static final String ANSI_CYAN = "\u001B[36m";
    /**
     * Cyan background
     */
    public static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
    /**
     * Green text
     */
    public static final String ANSI_GREEN = "\u001B[32m";
    /**
     * Green background
     */
    public static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
    /**
     * Purple text
     */
    public static final String ANSI_PURPLE = "\u001B[35m";
    /**
     * Purple background
     */
    public static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    /**
     * Red text
     */
    public static final String ANSI_RED = "\u001B[31m";
    /**
     * Red background
     */
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";
    /**
     * Reset style
     */
    public static final String ANSI_RESET = "\u001B[0m";
    /**
     * White text
     */
    public static final String ANSI_WHITE = "\u001B[37m";
    /**
     * White background
     */
    public static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
    /**
     * Yellow text
     */
    public static final String ANSI_YELLOW = "\u001B[33m";
    /**
     * Yellow background
     */
    public static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static int LOG_LEVEL = 0;
    private static int LOG_LEVEL_FILE = 1;
    private static PrintWriter printWriter = null;

    static {
        try {
            File logDirectory = new File("logs");
            if (!logDirectory.exists() || !logDirectory.isDirectory()) {
                //noinspection ResultOfMethodCallIgnored
                logDirectory.mkdir();
            }
            File file = new File("logs/latest.log");
            File targetFile = null;
            if (file.exists()) {
                long i = 0;
                targetFile = new File("logs/" + i + ".log");
                File targetFileZip = new File("logs/" + i + ".log.zip");
                while (targetFile.exists() || targetFileZip.exists()) {
                    if (targetFile.exists()) {
                        compressFile(targetFile);
                        //noinspection ResultOfMethodCallIgnored
                        targetFile.delete();
                    }
                    i++;
                    targetFile = new File("logs/" + i + ".log");
                    targetFileZip = new File("logs/" + i + ".log.zip");
                }
            }
            if (targetFile != null) {
                FileUtils.moveFile(file, targetFile);
                compressFile(targetFile);
                //noinspection ResultOfMethodCallIgnored
                targetFile.delete();
            }
            printWriter = new PrintWriter(new FileOutputStream(file));
        } catch (IOException e) {
            warning("Failed to setup log file!");
            warning(e);
        }
    }

    /**
     * Logs message with info level.
     *
     * @param message Message to log
     */
    public static void warning(String message) {
        warningL(message);
    }

    /**
     * Logs message with info level.
     *
     * @param message Message to log
     */
    private static void warningL(String message) {
        String x = ANSI_RESET + ANSI_YELLOW + "[WARNING][" + new Date() + "] " + message + ANSI_RESET;
        String x2 = ANSI_RESET + ANSI_YELLOW + "[WARNING][" + new Date() + "][" + getMethodIdentifier(1) + "] " + message + ANSI_RESET;
        if (LOG_LEVEL < 3)
            System.out.println(x);
        if (LOG_LEVEL_FILE < 3 && printWriter != null) printWriter.println(x2);
    }

    /**
     * Logs message with error level.
     *
     * @param message Message to log
     */
    public static void error(String message) {
        errorL(message);
    }

    /**
     * Logs message with error level.
     *
     * @param message Message to log
     */
    private static void errorL(String message) {
        String x = ANSI_RESET + ANSI_RED + "[ERROR][" + new Date() + "] " + message + ANSI_RESET;
        String x2 = ANSI_RESET + ANSI_RED + "[ERROR][" + new Date() + "][" + getMethodIdentifier(1) + "] " + message + ANSI_RESET;
        if (LOG_LEVEL < 4)
            System.out.println(x);
        if (LOG_LEVEL_FILE < 4 && printWriter != null) printWriter.println(x2);
    }

    /**
     * Logs exception with debug level.
     *
     * @param e Exception to log
     */
    public static void debug(Throwable e) {
        debugL(exceptionToString(e));
    }

    /**
     * Logs message with debug level.
     *
     * @param message Message to log
     */
    private static void debugL(String message) {
        String x = ANSI_RESET + ANSI_CYAN + "[DEBUG][" + new Date() + "] " + message + ANSI_RESET;
        String x2 = ANSI_RESET + ANSI_CYAN + "[DEBUG][" + new Date() + "][" + getMethodIdentifier(1) + "] " + message + ANSI_RESET;
        if (LOG_LEVEL < 1)
            System.out.println(x);
        if (LOG_LEVEL_FILE < 1 && printWriter != null) printWriter.println(x2);
    }

    /**
     * Logs exception with info level.
     *
     * @param e Exception to log
     */
    public static void log(Throwable e) {
        logL(exceptionToString(e));
    }

    /**
     * Logs message with debug level.
     *
     * @param message Message to log
     */
    private static void logL(String message) {
        String x = ANSI_RESET + ANSI_WHITE + "[INFO][" + new Date() + "] " + message + ANSI_RESET;
        String x2 = ANSI_RESET + ANSI_WHITE + "[INFO][" + new Date() + "][" + getMethodIdentifier(1) + "] " + message + ANSI_RESET;
        if (LOG_LEVEL < 2)
            System.out.println(x);
        if (LOG_LEVEL_FILE < 2 && printWriter != null) printWriter.println(x2);
    }

    /**
     * Logs message with debug level.
     *
     * @param message Message to log
     */
    public static void debug(String message) {
        debugL(message);
    }

    /**
     * Logs exception with warning level.
     *
     * @param e Exception to log
     */
    public static void warning(Throwable e) {
        warningL(exceptionToString(e));
    }

    /**
     * Logs exception with error level.
     *
     * @param e Exception to log
     */
    public static void error(Throwable e) {
        errorL(exceptionToString(e));
    }

    /**
     * Logs message with fatal level and exist application.
     *
     * @param message Message to log
     */
    public static void fatal(String message) {
        fatalL(message);
    }

    /**
     * Logs message with fatal level and exist application.
     *
     * @param message Message to log
     */
    private static void fatalL(String message) {
        String x = ANSI_RESET + ANSI_WHITE_BACKGROUND + ANSI_RED + "[FATAL][" + new Date() + "] " + message + ANSI_RESET;
        String x2 = ANSI_RESET + ANSI_WHITE_BACKGROUND + ANSI_RED + "[FATAL][" + new Date() + "][" + getMethodIdentifier(1) + "] " + message + ANSI_RESET;
        if (LOG_LEVEL < 5)
            System.out.println(x);
        if (LOG_LEVEL_FILE < 5 && printWriter != null) printWriter.println(x2);
        flushLog();
        Runtime.getRuntime().halt(2);
    }

    /**
     * Flushes log file
     */
    public static void flushLog() {
        if (printWriter != null) printWriter.flush();
    }

    /**
     * Logs exception with fatal level.
     *
     * @param e Exception to log
     */
    public static void fatal(Throwable e) {
        fatalL(exceptionToString(e));
    }

    /**
     * @return File log level
     */
    public static int getLogLevelFile() {
        return LOG_LEVEL_FILE;
    }

    /**
     * @param logLevelFile 0 - Debug, 1 - Info, 2 - Warning, 3 - Error, 4 - Fatal, 5 - Off
     */
    public static void setLogLevelFile(int logLevelFile) {
        LOG_LEVEL_FILE = logLevelFile;
    }

    /**
     * @return Log level
     */
    public static int getLogLevel() {
        return LOG_LEVEL;
    }

    /**
     * @param logLevel 0 - Debug, 1 - Info, 2 - Warning, 3 - Error, 4 - Fatal, 5 - Off
     */
    public static void setLogLevel(int logLevel) {
        LOG_LEVEL = logLevel;
    }

    /**
     * Logs message with debug level.
     *
     * @param message Message to log
     */
    public static void log(String message) {
        logL(message);
    }
}
