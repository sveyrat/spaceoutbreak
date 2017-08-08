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
            Logger.getInstance().error(getClass(), message, e);
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
        // Game over
        if (gameFinished()) {
            Logger.getInstance().info(getClass(), "Game over, go to END");
            return RoundPhase.END;
        }

        Game currentGame = currentGame();

        if (isFirstRound() && currentGame.getCaptain() == null) {
            Logger.getInstance().info(getClass(), "First round and no captain yet, go to CAPTAIN_ELECTION");
            return RoundPhase.CAPTAIN_ELECTION;
        }

        if (nightPhaseStarted() && nightPhaseNotFinished()) {
            Logger.getInstance().info(getClass(), "Night phase has been started but not finished, go to NIGHT");
            return RoundPhase.NIGHT;
        }

        // The only case where a vote can not lead straight to a result is in the case of a draw
        if (votingPhaseStarted() && RepositoryManager.getInstance().voteRepository().voteResult().draw()) {
            Logger.getInstance().info(getClass(), "Voting phase has been started but not finished (due to a draw), go to VOTE");
            return RoundPhase.VOTE;
        }

        if (!nightPhaseStarted()) {
            Logger.getInstance().info(getClass(), "Night phase has not been done yet, go to NIGHT");
            return RoundPhase.NIGHT;
        }

        // Reaching this point means that the night phase has been started AND finished

        if (nightAutospyRequiredAndNotFinished()) {
            Logger.getInstance().info(getClass(), "Night autopsy has not been done yet, go to NIGHT_AUTOPSY");
            return RoundPhase.NIGHT_AUTOPSY;
        }

        // The captain could have been killed in the voting phase, in which case we'll want to do the autopsy before electing a new captain (see below)
        if (!votingPhaseStarted() && !currentGame.getCaptain().isAlive()) {
            Logger.getInstance().info(getClass(), "Dead captain before voting phase, go to CAPTAIN_ELECTION");
            return RoundPhase.CAPTAIN_ELECTION;
        }

        if (!votingPhaseStarted()) {
            Logger.getInstance().info(getClass(), "Voting phase has not been done yet, go to VOTE");
            return RoundPhase.VOTE;
        }

        // Reaching this point means that the voting phase has been started AND finished

        if (dayAutopsyRequiredAndNotFinished()) {
            Logger.getInstance().info(getClass(), "Day autopsy has not been done yet, go to DAY_AUTOPSY");
            return RoundPhase.DAY_AUTOPSY;
        }

        // Captain has been killed in voting phase (see above)
        if (!currentGame.getCaptain().isAlive()) {
            Logger.getInstance().info(getClass(), "Dead captain after vote, go to CAPTAIN_ELECTION");
            return RoundPhase.CAPTAIN_ELECTION;
        }

        Logger.getInstance().info(getClass(), "Previous round has been completed, go to NEW_ROUND");
        return RoundPhase.NEW_ROUND;
    }

    private boolean isFirstRound() {
        return currentRound().getOrder() == 1;
    }

    private boolean dayAutopsyRequiredAndNotFinished() {
        return !currentRound().isDayAutopsyDone() && RepositoryManager.getInstance().voteRepository().voteResult().mostVotedFor() != null;
    }

    private boolean nightAutospyRequiredAndNotFinished() {
        return !currentRound().isNightAutopsyDone() && RepositoryManager.getInstance().nightActionRepository().killedDuringNightPhase().size() > 0;
    }

    private boolean nightPhaseNotFinished() {
        NightStepManager nightStepManager = RepositoryManager.getInstance().nightActionRepository().nextNightStep();
        if (nightStepManager != null) {
            return true;
        }
        return false;
    }

    private boolean votingPhaseStarted() {
        Round currentRound = currentRound();
        return currentRound.getVotes() != null && !currentRound.getVotes().isEmpty();
    }

    private boolean nightPhaseStarted() {
        List<NightAction> nightActions = new ArrayList<>(currentRound().getNightActions());
        return nightActions != null && !nightActions.isEmpty();
    }

    public void markNightAutopsyAsDone() {
        Round currentRound = currentRound();
        currentRound.setNightAutopsyDone(true);
        try {
            roundDao().update(currentRound);
            Logger.getInstance().info(getClass(), "Night autopsy done");
        } catch (SQLException e) {
            String message = "Error while marking night autopsy as done";
            Logger.getInstance().error(getClass(), message, e);
            throw new RuntimeException(message, e);
        }
    }

    public void markDayAutopsyAsDone() {
        Round currentRound = currentRound();
        currentRound.setDayAutopsyDone(true);
        try {
            roundDao().update(currentRound);
            Logger.getInstance().info(getClass(), "Day autopsy done");
        } catch (SQLException e) {
            String message = "Error while marking day autopsy as done";
            Logger.getInstance().error(getClass(), message, e);
            throw new RuntimeException(message, e);
        }
    }
}
