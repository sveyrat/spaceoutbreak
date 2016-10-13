package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.ArrayList;
import java.util.List;

public class MutantsParalyzeOrInfectStepManager extends StepManager {

    private List<Player> paralyzedPlayers = new ArrayList<>();
    private List<Player> infectedPlayers = new ArrayList<>();

    /*
     * Those come from the previous step and are required to display the after step text
     * (who to mutate or kill)
     */
    private Player mutedPlayer;
    private Player killedPlayer;

    public MutantsParalyzeOrInfectStepManager(Player mutedPlayer, Player killedPlayer) {
        super(R.string.night_basis_step_paralyse_headerText);
        this.mutedPlayer = mutedPlayer;
        this.killedPlayer = killedPlayer;
    }

    @Override
    public void select(Context context, final ImageView selectedImageView, final Player player) {
        boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            paralyzedPlayers.remove(player);
            infectedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String alertMessage = String.format(context.getResources().getString(R.string.common_player_name), player.getName());
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.night_basis_action_paralyse), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                paralyzedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.night_basis_action_infect), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                infectedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public boolean validateStep(Context context) {
        int numberOfSelectedPlayers = paralyzedPlayers.size() + infectedPlayers.size();
        if (numberOfSelectedPlayers == 0) {
            showErrorToast(context, R.string.night_basis_common_error_selectAtLeastOne);
            return false;
        }
        if (numberOfSelectedPlayers > 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectAtMostOne);
            return false;
        }

        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        if (infectedPlayers.size() > 0) {
            nightActionRepository.infect(infectedPlayers.get(0));
            return true;
        }
        nightActionRepository.paralyze(paralyzedPlayers.get(0));
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
        Player paralyzedPlayer = paralyzedPlayers.size() > 0 ? paralyzedPlayers.get(0) : null;
        Player infectedPlayer = infectedPlayers.size() > 0 ? infectedPlayers.get(0) : null;
        if (paralyzedPlayer != null) {
            instructions += context.getResources().getString(R.string.night_basis_action_paralyse) + " " + paralyzedPlayer.getName();
        } else {
            instructions += context.getResources().getString(R.string.night_basis_action_infect) + " " + infectedPlayer.getName();
        }
        return instructions;
    }
}