package com.github.sveyrat.spaceoutbreak.dao.repository;

import com.github.sveyrat.spaceoutbreak.dao.DatabaseOpenHelper;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.SpyInspectionResult;
import com.github.sveyrat.spaceoutbreak.display.nightaction.ComputerScientistNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.DoctorsHealOrKillNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.GeneticistNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.HackerNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.MutantsMutateOrKillNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.MutantsParalyzeNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.PsychologistNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.SpyNightStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.NightStepManager;
import com.github.sveyrat.spaceoutbreak.domain.Game;
import com.github.sveyrat.spaceoutbreak.domain.NightAction;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.Round;
import com.github.sveyrat.spaceoutbreak.domain.constant.Genome;
import com.github.sveyrat.spaceoutbreak.domain.constant.NightActionType;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.github.sveyrat.spaceoutbreak.log.Logger;
import com.github.sveyrat.spaceoutbreak.util.DataHolderUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NightActionRepository extends AbstractRepository {

    public NightActionRepository(DatabaseOpenHelper databaseOpenHelper) {
        super(databaseOpenHelper);
    }

    /**
     * Creates a new round for the current game.
     * If any players where paralysed, resets its state.
     * The created round automatically becomes the current round.
     */
    public void newRound() {
        try {
            Game game = currentGame();

            // Log all player informations
            Logger.getInstance().info(NightActionRepository.class.getName(), "------------------------- Players state start -------------------------");
            for (Player player : game.getPlayers()) {
                Logger.getInstance().info(NightActionRepository.class.getName(), player.toString());
            }
            Logger.getInstance().info(NightActionRepository.class.getName(), "------------------------- Players state end -------------------------");

            Round round = new Round(game);
            roundDao().create(round);

            for (Player player : round.getGame().getPlayers()) {
                player.setParalyzed(false);
                playerDao().update(player);
            }

            DataHolderUtil.getInstance().setCurrentRoundId(round.getId());

            Logger.getInstance().info(NightActionRepository.class.getName(), "Created new round " + round.getId() + " for game " + game.getId());
        } catch (SQLException e) {
            String message = "Error while attempting to create a new round for game with id " + DataHolderUtil.getInstance().getCurrentGameId();
            Logger.getInstance().info(NightActionRepository.class.getName(), message);
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
            Logger.getInstance().info(NightActionRepository.class.getName(), "Can not mutate player " + player.getName() + " with id " + player.getId() + ". Mutant : " + player.isMutant() + ". Alive : " + player.isAlive());
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

            Logger.getInstance().info(NightActionRepository.class.getName(), "Mutated " + player.getName());
        } catch (SQLException e) {
            String message = "Error while attempting to mutate player " + player.getName() + " with id " + player.getId();
            Logger.getInstance().info(NightActionRepository.class.getName(), message);
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
            Logger.getInstance().error(NightActionRepository.class.getName(), "Can not kill player " + player.getName() + " with id " + player.getId() + " because he is already dead.");
            return;
        }
        if (Role.BASE_MUTANT != killerRole && Role.DOCTOR != killerRole) {
            Logger.getInstance().error(NightActionRepository.class.getName(), "Can not kill player " + player.getName() + " with id " + player.getId() + " because a " + killerRole + " is not supposed to kill anybody.");
            return;
        }
        try {
            playerDao().refresh(player);
            player.setAlive(false);
            playerDao().update(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, killerRole, NightActionType.KILL, player);
            nightActionDao().create(nightAction);

            Logger.getInstance().info(NightActionRepository.class.getName(), "Killed " + player.getName());
        } catch (SQLException e) {
            String message = "Error while attempting to kill player " + player.getName() + " with id " + player.getId();
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
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
            Logger.getInstance().error(NightActionRepository.class.getName(), "Can not paralyse player " + player.getName() + " with id " + player.getId() + " because he is dead.");
            return;
        }
        try {
            playerDao().refresh(player);
            player.setParalyzed(true);
            playerDao().update(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.BASE_MUTANT, NightActionType.PARALYSE, player);
            nightActionDao().create(nightAction);

            Logger.getInstance().info(NightActionRepository.class.getName(), "Paralyzed " + player.getName());
        } catch (SQLException e) {
            String message = "Error while attempting to paralyse player " + player.getName() + " with id " + player.getId();
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
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
            Logger.getInstance().error(NightActionRepository.class.getName(), "Can not heal player " + player.getName() + " with id " + player.getId() + " because he is dead.");
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

            Logger.getInstance().info(NightActionRepository.class.getName(), "Healed " + player.getName());
        } catch (SQLException e) {
            String message = "Error while attempting to heal player " + player.getName() + " with id " + player.getId();
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * @return the number of mutants alive in the current game
     */
    public int countMutantsForComputerScientist() {
        try {
            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.COMPUTER_SCIENTIST, NightActionType.INSPECT, null);
            nightActionDao().create(nightAction);

            Logger.getInstance().info(NightActionRepository.class.getName(), "Computer scientist got the mutants count");
        } catch (SQLException e) {
            String message = "Error while attempting to save computer scientist count mutants action";
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
        return RepositoryManager.getInstance().gameInformationRepository().countMutantsInCurrentGame();
    }

    /**
     * Tests if a player is a mutant.
     *
     * @param player the player to test
     * @return whether the player is a mutant
     */
    public boolean testIfMutantForPsychologist(Player player) {
        try {
            playerDao().refresh(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.PSYCHOLOGIST, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            Logger.getInstance().info(NightActionRepository.class.getName(), "Player " + player.getName() + " has been inspected by the psychologist");

            return player.isMutant();
        } catch (SQLException e) {
            String message = "Error while attempting to test if player with id " + player.getId() + " is a mutant";
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Tests the genome of a player
     *
     * @param player the player to test
     * @return the genome of the player
     */
    public Genome testGenomeForGeneticist(Player player) {
        try {
            playerDao().refresh(player);

            Round round = currentRound();
            NightAction nightAction = new NightAction(round, Role.GENETICIST, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            Logger.getInstance().info(NightActionRepository.class.getName(), "Player " + player.getName() + " has been inspected by the geneticist");

            return player.getGenome();
        } catch (SQLException e) {
            String message = "Error while attempting to test genome of player with id " + player.getId();
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
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
            Round currentRound = currentRound();
            NightAction nightAction = new NightAction(currentRound, Role.SPY, NightActionType.INSPECT, player);
            nightActionDao().create(nightAction);

            List<NightAction> actions = nightActionDao().queryBuilder()//
                    .where().eq("targetPlayer_id", player.getId())//
                    .and().eq("round_id", currentRound.getId())//
                    .query();

            Logger.getInstance().info(NightActionRepository.class.getName(), "Player " + player.getName() + " has been spied on");

            return new SpyInspectionResult(actions);
        } catch (SQLException e) {
            String message = "Error while attempting to inspect actions targeted at player with id " + player.getId();
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * Hacks the role of computer scientist
     *
     * @return the number of mutant alive
     */
    public Integer hackComputerScientist() {
        try {
            NightAction hackedRoleNightAction = nightActionForHacker(Role.COMPUTER_SCIENTIST);
            return hackedRoleNightAction == null ? null : RepositoryManager.getInstance().gameInformationRepository().countMutantsInCurrentGame();
        } catch (SQLException e) {
            String message = "Error while attempting to hack computer scientist";
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    /**
     * @return the player inspected by the psychologist, or null if there was none
     */
    public Player hackPsychologist() {
        return hack(Role.PSYCHOLOGIST);
    }

    /**
     * @return the player inspected by the geneticist, or null if there was none
     */
    public Player hackGeneticist() {
        return hack(Role.GENETICIST);
    }

    private Player hack(Role role) {
        try {
            NightAction hackedRoleNightAction = nightActionForHacker(role);
            return hackedRoleNightAction == null ? null : hackedRoleNightAction.getTargetPlayer();
        } catch (SQLException e) {
            String message = "Error while attempting to hack " + role;
            Logger.getInstance().error(NightActionRepository.class.getName(), message);
            throw new RuntimeException(message, e);
        }
    }

    private NightAction nightActionForHacker(Role role) throws SQLException {
        Round currentRound = currentRound();

        NightAction hackedRoleNightAction = nightActionDao().queryBuilder()//
                .where().eq("round_id", currentRound.getId())//
                .and().eq("actingPlayerRole", role).queryForFirst();

        NightAction nightAction = new NightAction(currentRound, Role.HACKER, NightActionType.INSPECT, null);
        nightAction.setHackedRole(role);
        nightActionDao().create(nightAction);

        Logger.getInstance().info(NightActionRepository.class.getName(), role.toString() + " has been hacked");
        return hackedRoleNightAction;
    }

    public NightStepManager nextNightStep() {
        List<NightAction> nightActions = new ArrayList<>(currentRound().getNightActions());
        if (nightActions == null || nightActions.isEmpty()) {
            // newly created night round
            Logger.getInstance().info(getClass().getName(), "No night actions have been made yet in this round, meaning it's the mutants turn to play");
            return new MutantsMutateOrKillNightStepManager();
        }
        NightAction latestAction = nightActions.get(nightActions.size() - 1);
        Role lastPlayedRole = latestAction.getActingPlayerRole();
        if (lastPlayedRole == null) {
            Logger.getInstance().info(getClass().getName(), "No role has been played yet in this round, meaning it's the mutants turn to play");
            return new MutantsMutateOrKillNightStepManager();
        }

        switch (lastPlayedRole) {
            case BASE_MUTANT:
                if (latestAction.getType() == NightActionType.MUTATE) {
                    Logger.getInstance().info(getClass().getName(), "Last action was mutants mutating, so it's the mutants turn to paralyse");
                    return new MutantsParalyzeNightStepManager(latestAction.getTargetPlayer(), null);
                }
                if (latestAction.getType() == NightActionType.KILL) {
                    Logger.getInstance().info(getClass().getName(), "Last action was mutants kill, so it's the mutants turn to paralyse");
                    return new MutantsParalyzeNightStepManager(null, latestAction.getTargetPlayer());
                }
                if (canBePlayed(Role.DOCTOR)) {
                    Logger.getInstance().info(getClass().getName(), "Last role played was the mutants, it's the doctors' turn");
                    int numberOfHeals = numberOfHealsAvailable();
                    return new DoctorsHealOrKillNightStepManager(fakeStep(Role.DOCTOR), numberOfHeals);
                }
                // otherwise, keep going (no break)
            case DOCTOR:
                if (canBePlayed(Role.COMPUTER_SCIENTIST)) {
                    Logger.getInstance().info(getClass().getName(), "It's the computer scientist's turn");
                    return new ComputerScientistNightStepManager(fakeStep(Role.COMPUTER_SCIENTIST));
                }
                // otherwise, keep going (no break)
            case COMPUTER_SCIENTIST:
                if (canBePlayed(Role.PSYCHOLOGIST)) {
                    Logger.getInstance().info(getClass().getName(), "It's the psychologist's turn");
                    return new PsychologistNightStepManager(fakeStep(Role.PSYCHOLOGIST));
                }
                // otherwise, keep going (no break)
            case PSYCHOLOGIST:
                if (canBePlayed(Role.GENETICIST)) {
                    Logger.getInstance().info(getClass().getName(), "It's the geneticist's turn");
                    return new GeneticistNightStepManager(fakeStep(Role.GENETICIST));
                }
                // otherwise, keep going (no break)
            case GENETICIST:
                if (canBePlayed(Role.SPY)) {
                    Logger.getInstance().info(getClass().getName(), "It's the spy's turn");
                    return new SpyNightStepManager(fakeStep(Role.SPY));
                }
                // otherwise, keep going (no break)
            case SPY:
                if (canBePlayed(Role.HACKER)) {
                    Logger.getInstance().info(getClass().getName(), "It's the hacker's turn");
                    return new HackerNightStepManager(fakeStep(Role.HACKER));
                }
                // otherwise, keep going (no break)
            case HACKER:
                // No next step : the activity should change
                return null;
        }

        String message = "Could not determine the next step of the night round";
        Logger.getInstance().error(NightActionRepository.class.getName(), message);
        throw new RuntimeException(message);
    }

    private boolean canBePlayed(Role role) {
        GameInformationRepository gameInformationRepository = RepositoryManager.getInstance().gameInformationRepository();
        for (Player player : gameInformationRepository.loadAlivePlayers()) {
            // The paralyzed and mutant cases are not handled here, since the GM has to do "fake" rounds to avoid leaking information
            if (player.getRole() == role) {
                return true;
            }
        }
        return false;
    }

    private boolean fakeStep(Role role) {
        GameInformationRepository gameInformationRepository = RepositoryManager.getInstance().gameInformationRepository();
        List<Player> alivePlayers = gameInformationRepository.loadAlivePlayers();
        // There can be more than one doctor, so here we check if there is at least one that is neither paralyzed nor mutant
        if (role == Role.DOCTOR) {
            for (Player player : alivePlayers) {
                if (player.getRole() == Role.DOCTOR //
                        && !player.isParalyzed()
                        && !player.isMutant()) {
                    return false;
                }
            }
            return true;
        }

        for (Player player : alivePlayers) {
            if (player.getRole() != role) {
                continue;
            }
            if (player.isParalyzed()) {
                return true;
            }
        }
        return false;
    }

    private int numberOfHealsAvailable() {
        GameInformationRepository gameInformationRepository = RepositoryManager.getInstance().gameInformationRepository();
        List<Player> alivePlayers = gameInformationRepository.loadAlivePlayers();
        int numberOfDoctorsPlaying = 0;
        for (Player player : alivePlayers) {
            if (player.getRole() == Role.DOCTOR
                    && !player.isMutant()
                    && !player.isParalyzed()) {
                numberOfDoctorsPlaying++;
            }
        }
        return numberOfDoctorsPlaying;
    }
}
