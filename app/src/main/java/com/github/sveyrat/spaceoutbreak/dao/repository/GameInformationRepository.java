package com.github.sveyrat.spaceoutbreak.dao.repository;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundStep;
import com.github.sveyrat.spaceoutbreak.display.nightaction.StepManager;
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
    private boolean isGameFinished() {
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
    public RoundStep nextStep() {
        if (isGameFinished()) {
            return RoundStep.END;
        }
        Game currentGame = currentGame();
        Round currentRound = currentRound();
        List<NightAction> nightActions = new ArrayList<>(currentRound.getNightActions());
        if (nightActions != null && !nightActions.isEmpty()) {
            StepManager stepManager = RepositoryManager.getInstance().nightActionRepository().nextStep(nightActions.get(nightActions.size() - 1).getActingPlayerRole());
            if (stepManager != null) {
                // Night step has been started but not finished
                Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is NIGHT");
                return RoundStep.NIGHT;
            }
        }
        if (currentRound.getVotes() != null && !currentRound.getVotes().isEmpty() && RepositoryManager.getInstance().voteRepository().voteResult().draw()) {
            // Day voting step has been started but not finished
            Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is DAY");
            return RoundStep.DAY;
        }
        if (currentGame.getCaptain() == null || !currentGame.getCaptain().isAlive()) {
            Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is CAPTAIN ELECTION");
            return RoundStep.CAPTAIN_ELECTION;
        }
        if (nightActions == null || nightActions.isEmpty()) {
            Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is NIGHT");
            return RoundStep.NIGHT;
        }
        if (currentRound.getVotes() == null || currentRound.getVotes().isEmpty()) {
            Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is DAY");
            return RoundStep.DAY;
        }
        Logger.getInstance().info(GameInformationRepository.class.getName(), "Next round is NEW");
        return RoundStep.NEW;
    }
}
