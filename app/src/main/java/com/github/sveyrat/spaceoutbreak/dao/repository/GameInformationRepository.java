package com.github.sveyrat.spaceoutbreak.dao.repository;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundStep;
import com.github.sveyrat.spaceoutbreak.display.nightaction.StepManager;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;

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
        Round currentRound = currentRound();
        List<NightAction> nightActions = new ArrayList<>(currentRound.getNightActions());
        if (nightActions != null && !nightActions.isEmpty()) {
            StepManager stepManager = RepositoryManager.getInstance().nightActionRepository().nextStep(nightActions.get(nightActions.size() - 1).getActingPlayerRole());
            if (stepManager != null) {
                // Night step has been started but not finished
                return RoundStep.NIGHT;
            }
        }
        if (currentRound.getVotes() != null && !currentRound.getVotes().isEmpty() && RepositoryManager.getInstance().voteRepository().voteResult().draw()) {
            // Day voting step has been started but not finished
            return RoundStep.DAY;
        }
        if (currentRound.getCaptain() == null || !currentRound.getCaptain().isAlive()) {
            return RoundStep.CAPTAIN_ELECTION;
        }
        if (nightActions == null || nightActions.isEmpty()) {
            return RoundStep.NIGHT;
        }
        if (currentRound.getVotes() == null || currentRound.getVotes().isEmpty()) {
            return RoundStep.DAY;
        }
        return RoundStep.NEW;
    }
}
