package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;

public class MutantsParalyzeStepManager extends StepManager {

    /*
     * Those come from the previous step and are required to display the after step text
     * (who to mutate or kill)
     */
    private Player mutedPlayer;
    private Player killedPlayer;

    public MutantsParalyzeStepManager(Player mutedPlayer, Player killedPlayer) {
        super(R.string.night_basis_step_paralyse_headerText);
        this.mutedPlayer = mutedPlayer;
        this.killedPlayer = killedPlayer;
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOne);
            return false;
        }

        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        nightActionRepository.paralyze(selectedPlayers.get(0));
        return true;
    }

    @Override
    public StepManager nextStep() {
        return new DoctorsHealOrKillStepManager();
    }

    @Override
    public String afterStepText(Context context) {
        String instructions = "";
        if (mutedPlayer != null) {
            if (mutedPlayer.resistant()) {
                instructions += context.getResources().getString(R.string.night_basis_action_mutate_resistant) + " " + mutedPlayer.getName();
            } else {
                instructions += context.getResources().getString(R.string.night_basis_action_mutate) + " " + mutedPlayer.getName();
            }
        } else {
            instructions += context.getResources().getString(R.string.night_basis_action_kill) + " " + killedPlayer.getName();
        }
        instructions += "\n\n";
        instructions += context.getResources().getString(R.string.night_basis_action_paralyse) + " " + selectedPlayers.get(0).getName();
        return instructions;
    }
}
