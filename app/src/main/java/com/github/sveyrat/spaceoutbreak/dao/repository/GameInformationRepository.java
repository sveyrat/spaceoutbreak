package com.github.sveyrat.spaceoutbreak.dao.repository;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class GameInformationRepository extends AbstractRepository {

    public GameInformationRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Counts the total number of players in the current game.
     *
     * @return the number of players in the current game
     */
    public int countPlayers() {
        Game game = currentGame();
        return game.getPlayers().size();
    }

    /**
     * Counts the total number of mutant players in the current game.
     *
     * @return the number of mutant players in the current game
     */
    public int countMutantsInCurrentGame() {
        Game currentGame = currentGame();
        int numberOfMutants = 0;
        for (Player player : currentGame.getPlayers()) {
            if (player.isMutant() && player.isAlive()) {
                numberOfMutants++;
            }
        }
        return numberOfMutants;
    }

    /**
     * Loads all the players that are still alive.
     *
     * @return a list of all alive players in the current game
     */
    public List<Player> loadAlivePlayers() {
        Game game = currentGame();
        List<Player> alivePlayers = new ArrayList<>();
        for (Player player : game.getPlayers()) {
            if (player.isAlive()) {
                alivePlayers.add(player);
            }
        }
        Log.i(GameInformationRepository.class.getName(), "Loaded " + alivePlayers.size() + " alive players");
        return alivePlayers;
    }

    /**
     * Check if game is finished.
     *
     * @return a boolean of whether the game is finished
     */
    public boolean isGameFinished() {
        int numberOfMutants = countMutantsInCurrentGame();
        int numberOfAlivePlayers = loadAlivePlayers().size();

        if (numberOfMutants == numberOfAlivePlayers || numberOfMutants == 0) {
            return true;
        }
        return false;
    }

}
