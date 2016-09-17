package com.github.sveyrat.spaceoutbreak.dao;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.util.ArrayList;
import java.util.List;

public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "SpaceOutbreak.sqlite";
    private static final int DATABASE_VERSION = 1;

    private Dao<Game, Long> gameDao = null;
    private Dao<Player, Long> playerDao = null;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Game.class);
            TableUtils.createTable(connectionSource, Player.class);
        } catch (SQLException e) {
            Log.e(DatabaseOpenHelper.class.getName(), "Could not create database", e);
            throw new RuntimeException(e);
        } catch (java.sql.SQLException e) {
            Log.e(DatabaseOpenHelper.class.getName(), "Could not create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            // TODO : handle database migration between versions here
//            List<String> allSql = new ArrayList<>();
//            switch(oldVersion)
//            {
//                case 1:
//                    //allSql.add("alter table AdData add column `new_col` VARCHAR");
//                    //allSql.add("alter table AdData add column `new_col2` VARCHAR");
//            }
//            for (String sql : allSql) {
//                db.execSQL(sql);
//            }
        } catch (SQLException e) {
            Log.e(DatabaseOpenHelper.class.getName(), "Could not update database", e);
            throw new RuntimeException(e);
        }
    }

    public Dao<Game, Long> gameDao() {
        if (gameDao == null) {
            try {
                gameDao = getDao(Game.class);
            }catch (java.sql.SQLException e) {
                Log.e(DatabaseOpenHelper.class.getName(), "Could not create Game DAO", e);
                throw new RuntimeException(e);
            }
        }
        return gameDao;
    }

    public Dao<Player, Long> playerDao() {
        if (playerDao == null) {
            try {
                playerDao = getDao(Player.class);
            }catch (java.sql.SQLException e) {
                Log.e(DatabaseOpenHelper.class.getName(), "Could not create Player DAO", e);
                throw new RuntimeException(e);
            }
        }
        return playerDao;
    }
}