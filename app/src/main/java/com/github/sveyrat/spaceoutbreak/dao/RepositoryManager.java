package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Genome;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
