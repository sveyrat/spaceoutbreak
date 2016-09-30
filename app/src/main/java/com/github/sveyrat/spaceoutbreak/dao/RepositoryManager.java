package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;

import com.github.sveyrat.spaceoutbreak.dao.repository.GameInformationRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.InitGameRepository;

public class RepositoryManager {

    private static RepositoryManager instance;

    private static InitGameRepository initGameRepository;
    private static GameInformationRepository gameInformationRepository;
    private static NightActionRepository nightActionRepository;

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new RepositoryManager(ctx);
        }
    }

    public static RepositoryManager getInstance() {
        return instance;
    }

    private RepositoryManager(Context ctx) {
        DatabaseOpenHelper helper = new DatabaseOpenHelper(ctx);
        initGameRepository = new InitGameRepository(helper);
        gameInformationRepository = new GameInformationRepository(helper);
        nightActionRepository = new NightActionRepository(helper);
    }

    public InitGameRepository initGameRepository() {
        return initGameRepository;
    }

    public GameInformationRepository gameInformationRepository() {
        return gameInformationRepository;
    }

    public NightActionRepository nightActionRepository() {
        return nightActionRepository;
    }
}
