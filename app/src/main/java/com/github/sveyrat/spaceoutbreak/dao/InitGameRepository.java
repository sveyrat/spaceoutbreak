package com.github.sveyrat.spaceoutbreak.dao;

import android.util.Log;

import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.Genome;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Role;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class InitGameRepository {

    private DatabaseOpenHelper databaseOpenHelper;

    public InitGameRepository(DatabaseOpenHelper databaseOpenHelper) {
        this.databaseOpenHelper = databaseOpenHelper;
    }

    /**
     * Creates and persists a game with the given player names
     * @param playerNames the name of the players taking part in the game
     * @return the identifier of the game created
     */
    public Long createGameWithPlayers(List<String> playerNames) {
        try {
            Game game = new Game();
            databaseOpenHelper.gameDao().create(game);
            for (String playerName : playerNames) {
                databaseOpenHelper.playerDao().create(new Player(game, playerName));
            }
            Log.i(InitGameRepository.class.getName(), "Created game " + game.getId() + " with " + playerNames.size() + " players");
            return game.getId();
        } catch (SQLException e) {
            Log.e(InitGameRepository.class.getName(), "Error while attempting to create a game", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the roles and genomes randomly, and saves the results.
     *
     * @param gameId the identifier of the game
     * @param additionalRoles the roles to use, other than base mutant and doctor
     * @param drawRoles whether to randomly affect roles. If empty, method will not do anything
     * @param useGenomes whether to use genomes
     */
    public void initializeRoles(Long gameId, List<Role> additionalRoles, boolean drawRoles, boolean useGenomes) {
        if (!drawRoles) {
            return;
        }
        try {
            Game game = databaseOpenHelper.gameDao().queryForId(gameId);
            Collection<Player> players = game.getPlayers();
            // Reset roles and genomes to avoid weird situations if this method is called multiple times on the same game
            for (Player player : players) {
                player.setRole(Role.ASTRONAUT);
                player.setGenome(Genome.NORMAL);
                player.setMutant(false);
                databaseOpenHelper.playerDao().update(player);
            }
            drawRoles(players, additionalRoles);
            if (useGenomes) {
                drawGenomes(players);
            }
        } catch (SQLException e) {
            Log.e(InitGameRepository.class.getName(), "Error while attempting to initialize a game", e);
            throw new RuntimeException(e);
        }
    }

    private void drawRoles(Collection<Player> players, List<Role> roles) throws SQLException {
        List<Player> playersWithoutRole = new ArrayList<>(players);

        // Draw base mutant role
        Player baseMutant = affectRoleToRandomPlayer(playersWithoutRole, Role.BASE_MUTANT);
        // Base mutant is always host
        baseMutant.setGenome(Genome.HOST);
        baseMutant.setMutant(true);
        databaseOpenHelper.playerDao().update(baseMutant);
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
        databaseOpenHelper.playerDao().update(selectedPlayer);
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
        databaseOpenHelper.playerDao().update(selectedPlayer);
        players.remove(randomIndex);
        Log.i(InitGameRepository.class.getName(), "Affected genome " + genome + " to player " + selectedPlayer.getName());
    }

    public int countPlayers(Long gameId){
        Game game = null;
        int nbPlayers = 0;
        try {
            game = databaseOpenHelper.gameDao().queryForId(gameId);
            Collection<Player> players = game.getPlayers();
            nbPlayers = players.size();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nbPlayers;
    }
}
