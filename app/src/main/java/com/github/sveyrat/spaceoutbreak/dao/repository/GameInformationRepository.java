package com.github.sveyrat.spaceoutbreak.dao.repository;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundPhase;
import com.github.sveyrat.spaceoutbreak.display.nightaction.NightStepManager;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.log.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameInformationRepository extends AbstractRepository {

    public GameInformationRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * @return the list of all persisted games
     */
    public List<Game> lastTwoGames() {
        try {
            return new ArrayList<>(gameDao().queryBuilder().orderBy(Game.CREATION_DATE_FIELD_NAME, false).limit(2l).query());
        } catch (SQLException e) {
            String message = "Error while retrieving game list";
            Logger.getInstance().error(GameInformationRepository.class.getName(), message, e);
            throw new RuntimeException(message, e);
        }
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
        return alivePlayers;
    }

    /**
     * Check if game is finished.
     *
     * @return a boolean of whether the game is finished
     */
    private boolean gameFinished() {
        int numberOfMutants = countMutantsInCurrentGame();
        int numberOfAlivePlayers = loadAlivePlayers().size();

        if (numberOfMutants == numberOfAlivePlayers || numberOfMutants == 0) {
            return true;
        }
        return false;
    }

    /**
     * Determines the next round step for the current game.
     *
     * @return
     */
    public RoundPhase nextRoundPhase() {
        if (gameFinished()) {
            return RoundPhase.END;
        }
        Game currentGame = currentGame();
        Round currentRound = currentRound();
        List<NightAction> nightActions = new ArrayList<>(currentRound.getNightActions());
        if (nightActions != null && !nightActions.isEmpty()) {
            NightStepManager nightStepManager = RepositoryManager.getInstance().nightActionRepository().nextNightStep();
            if (nightStepManager != null) {
                // Night step has been started but not finished
                Logger.getInstance().info(getClass().getName(), "Night phase has been started but not finished, next phase is NIGHT");
                return RoundPhase.NIGHT;
            }
        }
        if (currentRound.getVotes() != null && !currentRound.getVotes().isEmpty() && RepositoryManager.getInstance().voteRepository().voteResult().draw()) {
            // Day voting step has been started but not finished
            Logger.getInstance().info(getClass().getName(), "Voting phase has been started but not finished due to a draw, next phase is VOTE");
            return RoundPhase.VOTE;
        }
        if (currentGame.getCaptain() == null || !currentGame.getCaptain().isAlive()) {
            Logger.getInstance().info(getClass().getName(), "No Captain or dead captain, next phase is CAPTAIN_ELECTION");
            return RoundPhase.CAPTAIN_ELECTION;
        }
        if (nightActions == null || nightActions.isEmpty()) {
            Logger.getInstance().info(getClass().getName(), "Night phase has not been done yet, next phase is NIGHT");
            return RoundPhase.NIGHT;
        }
        if (!currentRound.isNightAutopsyDone() && RepositoryManager.getInstance().nightActionRepository().killedDuringNightPhase().size() > 0) {
            Logger.getInstance().info(getClass().getName(), "Night autopsy has not been done yet, next phase is NIGHT_AUTOPSY");
            return RoundPhase.NIGHT_AUTOPSY;
        }
        if (currentRound.getVotes() == null || currentRound.getVotes().isEmpty()) {
            Logger.getInstance().info(getClass().getName(), "Voting phase has not been done yet, next phase is VOTE");
            return RoundPhase.VOTE;
        }
        if (!currentRound.isDayAutopsyDone() && RepositoryManager.getInstance().voteRepository().voteResult().mostVotedFor() != null) {
            Logger.getInstance().info(getClass().getName(), "Day autopsy has not been done yet, next phase is DAY_AUTOPSY");
            return RoundPhase.DAY_AUTOPSY;
        }
        Logger.getInstance().info(getClass().getName(), "Previous round has been completed, next phase is NEW_ROUND");
        return RoundPhase.NEW_ROUND;
    }

    public void markNightAutopsyAsDone() {
        Round currentRound = currentRound();
        currentRound.setNightAutopsyDone(true);
        try {
            roundDao().update(currentRound);
            Logger.getInstance().info(getClass().getName(), "Night autopsy done");
        } catch (SQLException e) {
            String message = "Error while marking night autopsy as done";
            Logger.getInstance().error(getClass().getName(), message, e);
            throw new RuntimeException(message, e);
        }
    }

    public void markDayAutopsyAsDone() {
        Round currentRound = currentRound();
        currentRound.setDayAutopsyDone(true);
        try {
            roundDao().update(currentRound);
            Logger.getInstance().info(getClass().getName(), "Day autopsy done");
        } catch (SQLException e) {
            String message = "Error while marking day autopsy as done";
            Logger.getInstance().error(getClass().getName(), message, e);
            throw new RuntimeException(message, e);
        }
    }
}
