package com.github.sveyrat.spaceoutbreak.util;

public class DataHolderUtil {

    private static DataHolderUtil instance;

    public static DataHolderUtil getInstance() {
        if (instance == null) {
            instance = new DataHolderUtil();
        }
        return instance;
    }

    private Long currentGameId;
    private Long currentRoundId;

    private DataHolderUtil() {
    }

    public Long getCurrentGameId() {
        return currentGameId;
    }

    public void setCurrentGameId(Long currentGameId) {
        this.currentGameId = currentGameId;
    }

    public Long getCurrentRoundId() {
        return currentRoundId;
    }

    public void setCurrentRoundId(Long currentRoundId) {
        this.currentRoundId = currentRoundId;
    }
}
