package com.github.sveyrat.spaceoutbreak.dao;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.dao.dto.SpyInspectionResult;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Genome;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.NightActionType;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Role;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.util.DataHolderUtil;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameRepository extends AbstractRepository {

    public GameRepository(DatabaseOpenHelper databaseOpenHelper) {
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
     * Creates a new round for the current game.
     * If any players where paralysed or infected, resets its state.
     * The created round automatically becomes the current round.
     */
    public void newRound() {
        try {
            Game game = currentGame();
            Round round = new Round(game);
            roundDao().create(round);

            for (Player player : round.getGame().getPlayers()) {
                player.setParalyzed(false);
                player.setInfected(false);
                playerDao().update(player);
            }

            DataHolderUtil.getInstance().setCurrentRoundId(round.getId());

            Log.i(GameRepository.class.getName(), "Created new round " + round.getId() + " for game " + game.getId());
        } catch (SQLException e) {
            String message = "Error while attempting to create a new round for game with id " + DataHolderUtil.getInstance().getCurrentGameId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Mutates a player if his/her status authorizes it.
     *
     * @param player the player to mutate
     */
    public void mutate(Player player) {
        if (player.isMutant() || !player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not mutate player " + player.getName() + " with id " + player.getId() + ". Mutant : " + player.isMutant() + ". Alive : " + player.isAlive());
            return;
        }
        try {
            playerDao().refresh(player);
            if (!player.resistant()) {
                player.setMutant(true);
                playerDao().update(player);
            }

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.BASE_MUTANT, NightActionType.MUTATE, player);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to mutate player " + player.getName() + " with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Kills a player.
     *
     * @param player     the player to kill
     * @param killerRole the role of the player or groups of players who decided to kill. Should be either mutant or doctors, otherwise the method will throw a runtime exception.
     */
    public void kill(Player player, Role killerRole) {
        if (!player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not kill player " + player.getName() + " with id " + player.getId() + " because he is already dead.");
            return;
        }
        if (Role.BASE_MUTANT != killerRole && Role.DOCTOR != killerRole) {
            Log.e(GameRepository.class.getName(), "Can not kill player " + player.getName() + " with id " + player.getId() + " because a " + killerRole + " is not supposed to kill anybody.");
            return;
        }
        try {
            playerDao().refresh(player);
            player.setAlive(false);
            playerDao().update(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, killerRole, NightActionType.KILL, player);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to kill player " + player.getName() + " with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Paralyses a player. This will be reverted once the round is completed by the round creation method.
     *
     * @param player the player to paralyse
     */
    public void paralyze(Player player) {
        if (!player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not paralyse player " + player.getName() + " with id " + player.getId() + " because he is dead.");
            return;
        }
        try {
            playerDao().refresh(player);
            player.setParalyzed(true);
            playerDao().update(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.BASE_MUTANT, NightActionType.PARALYSE, player);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to paralyse player " + player.getName() + " with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Infect a player. This will be reverted once the round is completed by the round creation method.
     *
     * @param player the player to infect
     */
    public void infect(Player player) {
        if (!player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not infect player " + player.getName() + " with id " + player.getId() + " because he is dead.");
            return;
        }
        try {
            playerDao().refresh(player);
            player.setInfected(true);
            playerDao().update(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.BASE_MUTANT, NightActionType.INFECT, player);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to paralyse player " + player.getName() + " with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Heals the player if possible.
     *
     * @param player the player to heal.
     */
    public void heal(Player player) {
        if (!player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not heal player " + player.getName() + " with id " + player.getId() + " because he is dead.");
            return;
        }
        try {
            playerDao().refresh(player);
            if (player.isMutant() && !player.host()) {
                player.setMutant(false);
                playerDao().update(player);
            }

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.DOCTOR, NightActionType.HEAL, player);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to heal player " + player.getName() + " with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * @return the number of mutants alive in the current game
     */
    public int countMutants() {
        Game currentGame = currentGame();
        int numberOfMutants = 0;
        for (Player player : currentGame.getPlayers()) {
            if (player.isMutant() && player.isAlive()) {
                numberOfMutants++;
            }
        }
        try {
            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.COMPUTER_SCIENTIST, NightActionType.INSPECT, null);
            nightActionDao().create(nightAction);
        } catch (SQLException e) {
            String message = "Error while attempting to save computer scientist count mutants action";
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
        return numberOfMutants;
    }

    /**
     * Tests if a player is a mutant.
     *
     * @param player the player to test
     * @return whether the player is a mutant
     */
    public boolean testIfMutant(Player player) {
        try {
            playerDao().refresh(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.PSYCHOLOGIST, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            return player.isMutant();
        } catch (SQLException e) {
            String message = "Error while attempting to test if player with id " + player.getId() + " is a mutant";
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Tests the genome of a player
     *
     * @param player the player to test
     * @return the genome of the player
     */
    public Genome testGenome(Player player) {
        try {
            playerDao().refresh(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.GENETICIST, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            return player.getGenome();
        } catch (SQLException e) {
            String message = "Error while attempting to test genome of player with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Retrieves what happened to the given player during the current round.
     *
     * @param player the player to inspect
     * @return the results of the inspection
     */
    public SpyInspectionResult inspectAsSpy(Player player) {
        try {
            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.SPY, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            List<NightAction> actions = nightActionDao().queryBuilder()//
                    .where().eq("target_player_id", player.getId())//
                    .query();
            return new SpyInspectionResult(actions);
        } catch (SQLException e) {
            String message = "Error while attempting to inspect actions targeted at player with id " + player.getId();
            Log.e(GameRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }
}
