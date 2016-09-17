package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;

public class RepositoryManager {

    private static RepositoryManager instance;

    private static InitGameRepository initGameRepository;

    public static void init(Context ctx) {
        if (instance == null) {
            instance = new RepositoryManager(ctx);
        }
    }

    public static RepositoryManager getInstance() {
        return instance;
    }

    private DatabaseOpenHelper helper;

    private RepositoryManager(Context ctx) {
        helper = new DatabaseOpenHelper(ctx);
        initGameRepository = new InitGameRepository(helper);
    }

    public InitGameRepository initGameRepository() {
        return initGameRepository;
    }
}
