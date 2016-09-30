package com.github.sveyrat.spaceoutbreak.dao;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;

import java.sql.SQLException;

public class GameRepository {

    private DatabaseOpenHelper databaseOpenHelper;

    public GameRepository(DatabaseOpenHelper databaseOpenHelper) {
        this.databaseOpenHelper = databaseOpenHelper;
    }

    public int countPlayers(Long gameId) {
        try {
            Game game = databaseOpenHelper.gameDao().queryForId(gameId);
            return game.getPlayers().size();
        } catch (SQLException e) {
            Log.e(GameRepository.class.getName(), "Error while attempting to count the number of players in game with id " + gameId);
            return 0;
        }
    }
}
