package com.github.sveyrat.spaceoutbreak.dao;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.NightActionType;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Role;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.util.DataHolderUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GameRepository extends AbstractRepository {

    public GameRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Counts the total number of players in a game.
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
                player.setParalysed(false);
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
    public void paralyse(Player player) {
        if (!player.isAlive()) {
            Log.e(GameRepository.class.getName(), "Can not paralyse player " + player.getName() + " with id " + player.getId() + " because he is dead.");
            return;
        }
        try {
            player.setParalysed(true);
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
}
