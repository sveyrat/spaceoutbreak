package com.github.sveyrat.spaceoutbreak.dao.repository;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.util.DataHolderUtil;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public abstract class AbstractRepository {

    private DatabaseOpenHelper databaseOpenHelper;

    public AbstractRepository(DatabaseOpenHelper databaseOpenHelper) {
        this.databaseOpenHelper = databaseOpenHelper;
    }

    protected Dao<Game, Long> gameDao() {
        return databaseOpenHelper.gameDao();
    }

    protected Dao<Player, Long> playerDao() {
        return databaseOpenHelper.playerDao();
    }

    protected Dao<Round, Long> roundDao() {
        return databaseOpenHelper.roundDao();
    }

    protected Dao<NightAction, Long> nightActionDao() {
        return databaseOpenHelper.nightActionDao();
    }

    protected Game currentGame() {
        try {
            return gameDao().queryForId(DataHolderUtil.getInstance().getCurrentGameId());
        } catch (SQLException e) {
            String message = "Error while attempting to load the current game with id " + DataHolderUtil.getInstance().getCurrentGameId();
            Log.e(AbstractRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    protected Round currentRound() {
        try {
            return roundDao().queryForId(DataHolderUtil.getInstance().getCurrentRoundId());
        } catch (SQLException e) {
            String message = "Error while attempting to load the current round with id " + DataHolderUtil.getInstance().getCurrentRoundId();
            Log.e(AbstractRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }
}
