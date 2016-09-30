package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;

public class RepositoryManager {

    private static RepositoryManager instance;

    private static InitGameRepository initGameRepository;
    private static GameRepository gameRepository;

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
        gameRepository = new GameRepository(helper);
    }

    public InitGameRepository initGameRepository() {
        return initGameRepository;
    }

    public GameRepository gameRepository() {
        return gameRepository;
    }
}
