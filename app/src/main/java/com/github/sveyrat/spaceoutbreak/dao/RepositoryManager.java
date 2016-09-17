package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.sql.SQLException;
import java.util.List;

public class RepositoryManager {

    private static RepositoryManager instance;

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
    }

    /**
     * Creates and persists a game with the given player names
     * @param playerNames the name of the players taking part in the game
     * @return the identifier of the game created
     */
    public Long createGameWithPlayers(List<String> playerNames) {
        try {
            Game game = new Game();
            helper.gameDao().create(game);
            for (String playerName : playerNames) {
                helper.playerDao().create(new Player(game, playerName));
            }
            return game.getId();
        } catch (SQLException e) {
            Log.e(RepositoryManager.class.getName(), "Error while attempting to create a game", e);
            throw new RuntimeException(e);
        }
    }
}
