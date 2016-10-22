package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;

import com.github.sveyrat.spaceoutbreak.dao.repository.GameInformationRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.InitGameRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.dao.repository.VoteRepository;

public class RepositoryManager {

    private static RepositoryManager instance;

    private static InitGameRepository initGameRepository;
    private static GameInformationRepository gameInformationRepository;
    private static NightActionRepository nightActionRepository;
    private static VoteRepository voteRepository;

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
        voteRepository = new VoteRepository(helper);
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

    public VoteRepository voteRepository() {
        return voteRepository;
    }
}
