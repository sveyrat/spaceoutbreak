package com.github.sveyrat.spaceoutbreak.log;


import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    private static final String LOG_PREFIX = "[SPACE_OUTBREAK] ";

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private static Logger instance;

    public static Logger getInstance() {
        if (instance == null) {
            instance = new Logger();
        }
        return instance;
    }

    private boolean logToFile;
    private File logFile;

    private Logger() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File appDirectory = new File(Environment.getExternalStorageDirectory() + "/SpaceOutbreak");
            File logDirectory = new File(appDirectory + "/log");

            // create app folder
            if (!appDirectory.exists()) {
                appDirectory.mkdir();
            }

            // create log folder
            if (!logDirectory.exists()) {
                logDirectory.mkdir();
            }

            logFile = new File(logDirectory, "logs-" + System.currentTimeMillis() + ".txt");
            logToFile = true;
        } else {
            Log.e(Logger.class.getName(), LOG_PREFIX + "No external storage mounted : application will not be able to log to file");
        }
    }

    public void info(Class clazz, String message) {
        String prefixedMessage = LOG_PREFIX + message;
        Log.i(clazz.getSimpleName(), prefixedMessage);
        if (!logToFile) {
            return;
        }
        logToFile(LogLevel.INFO, clazz, message);
    }

    public void error(Class clazz, String message) {
        String prefixedMessage = LOG_PREFIX + message;
        Log.e(clazz.getSimpleName(), prefixedMessage);
        if (!logToFile) {
            return;
        }
        logToFile(LogLevel.ERROR, clazz, message);
    }

    public void error(Class clazz, String message, Throwable t) {
        String prefixedMessage = LOG_PREFIX + message;
        Log.e(clazz.getSimpleName(), prefixedMessage, t);
        if (!logToFile) {
            return;
        }

        StringWriter stringWriter = new StringWriter();
        t.printStackTrace(new PrintWriter(stringWriter));
        String stackTrace = stringWriter.toString();

        String messageWithStacktrace = message + "\n" + stackTrace;

        logToFile(LogLevel.ERROR, clazz, messageWithStacktrace);
    }

    private void logToFile(LogLevel logLevel, Class clazz, String message) {
        String fullMessage = new SimpleDateFormat(DATE_FORMAT).format(new Date()) + " " + LOG_PREFIX + logLevel.getLogPrefix() + clazz.getSimpleName() + " " + message;
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(logFile, true));
            bufferedWriter.append(fullMessage);
            bufferedWriter.newLine();
        } catch (IOException e) {
            Log.e(Logger.class.getName(), "Error writing log to file", e);
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    Log.e(Logger.class.getName(), "Error trying to close file writer", e);
                }
            }
        }
    }
}
