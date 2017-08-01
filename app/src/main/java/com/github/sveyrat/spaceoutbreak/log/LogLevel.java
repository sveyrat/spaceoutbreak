package com.github.sveyrat.spaceoutbreak.log;

public enum LogLevel {
    INFO("INFO "),
    ERROR("ERROR ");

    private String logPrefix;

    LogLevel(String name) {
        this.logPrefix = name;
    }

    public String getLogPrefix() {
        return logPrefix;
    }
}
