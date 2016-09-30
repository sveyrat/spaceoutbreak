package com.github.sveyrat.spaceoutbreak.dao;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Genome;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Role;
import com.github.sveyrat.spaceoutbreak.util.DataHolderUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class InitGameRepository extends AbstractRepository {

    public InitGameRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Creates and persists a game with the given player names.
     * Created game automatically becomes the current game.
     *
     * @param playerNames the name of the players taking part in the game
     */
    public void createGameWithPlayers(List<String> playerNames) {
        try {
            Game game = new Game();
            gameDao().create(game);
            for (String playerName : playerNames) {
                playerDao().create(new Player(game, playerName));
            }
            DataHolderUtil.getInstance().setCurrentGameId(game.getId());
            Log.i(InitGameRepository.class.getName(), "Created game " + game.getId() + " with " + playerNames.size() + " players");
        } catch (SQLException e) {
            String message = "Error while attempting to create a game";
            Log.e(InitGameRepository.class.getName(), message, e);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Initializes the roles and genomes randomly, and saves the results.
     *
     * @param additionalRoles the roles to use, other than base mutant and doctor
     * @param drawRoles       whether to randomly affect roles. If empty, method will not do anything
     * @param useGenomes      whether to use genomes
     */
    public void initializeRoles(List<Role> additionalRoles, boolean drawRoles, boolean useGenomes) {
        if (!drawRoles) {
            return;
        }
        try {
            Game game = currentGame();
            Collection<Player> players = game.getPlayers();
            // Reset roles and genomes to avoid weird situations if this method is called multiple times on the same game
            for (Player player : players) {
                player.setRole(Role.ASTRONAUT);
                player.setGenome(Genome.NORMAL);
                player.setMutant(false);
                playerDao().update(player);
            }
            drawRoles(players, additionalRoles);
            if (useGenomes) {
                drawGenomes(players);
            }
        } catch (SQLException e) {
            String message = "Error while attempting to initialize a game";
            Log.e(InitGameRepository.class.getName(), message, e);
            throw new RuntimeException(message, e);
        }
    }

    private void drawRoles(Collection<Player> players, List<Role> roles) throws SQLException {
        List<Player> playersWithoutRole = new ArrayList<>(players);

        // Draw base mutant role
        Player baseMutant = affectRoleToRandomPlayer(playersWithoutRole, Role.BASE_MUTANT);
        // Base mutant is always host
        baseMutant.setGenome(Genome.HOST);
        baseMutant.setMutant(true);
        playerDao().update(baseMutant);
        // Draw 2 doctors
        affectRoleToRandomPlayer(playersWithoutRole, Role.DOCTOR);
        affectRoleToRandomPlayer(playersWithoutRole, Role.DOCTOR);
        // Draw other selected roles
        for (Role role : roles) {
            affectRoleToRandomPlayer(playersWithoutRole, role);
        }
    }

    private Player affectRoleToRandomPlayer(List<Player> players, Role role) throws SQLException {
        int randomIndex = new Random().nextInt(players.size());
        Player selectedPlayer = players.get(randomIndex);
        selectedPlayer.setRole(role);
        playerDao().update(selectedPlayer);
        players.remove(randomIndex);
        Log.i(InitGameRepository.class.getName(), "Affected role " + role + " to player " + selectedPlayer.getName());
        return selectedPlayer;
    }

    private void drawGenomes(Collection<Player> players) throws SQLException {
        List<Player> eligiblePlayers = new ArrayList<>(players);

        // Base mutant and doctors can not be assigned a genome
        List<Player> playersUnfitForGenome = new ArrayList<>();
        for (Player player : eligiblePlayers) {
            if (player.getRole() == Role.BASE_MUTANT || player.getRole() == Role.DOCTOR) {
                playersUnfitForGenome.add(player);
            }
        }
        eligiblePlayers.removeAll(playersUnfitForGenome);

        // Draw resistant and sensitive genomes
        affectGenome(eligiblePlayers, Genome.RESISTANT);
        affectGenome(eligiblePlayers, Genome.HOST);
    }

    private void affectGenome(List<Player> players, Genome genome) throws SQLException {
        int randomIndex = new Random().nextInt(players.size());
        Player selectedPlayer = players.get(randomIndex);
        selectedPlayer.setGenome(genome);
        playerDao().update(selectedPlayer);
        players.remove(randomIndex);
        Log.i(InitGameRepository.class.getName(), "Affected genome " + genome + " to player " + selectedPlayer.getName());
    }
}
