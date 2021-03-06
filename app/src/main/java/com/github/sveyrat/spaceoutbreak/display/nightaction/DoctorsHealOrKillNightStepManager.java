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

public class DoctorsHealOrKillNightStepManager extends NightStepManager {

    private Integer numberOfHeals;

    private List<Player> healedPlayers = new ArrayList<>();
    private List<Player> killedPlayers = new ArrayList<>();

    public DoctorsHealOrKillNightStepManager(boolean fakeStep, int numberOfHeals) {
        super(fakeStep, R.string.night_basis_step_healOrKill_headerText);
        this.numberOfHeals = numberOfHeals;
    }

    @Override
    public void select(Context context, final ImageView selectedImageView, final Player player) {
        boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            healedPlayers.remove(player);
            killedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String alertMessage = String.format(context.getResources().getString(R.string.common_player_name), player.getName());
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.night_basis_action_kill), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                killedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.night_basis_action_heal), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                healedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public boolean validateStep(Context context) {
        if (healedPlayers.size() == 0 && killedPlayers.size() == 0) {
            showErrorToast(context, R.string.night_basis_common_error_selectAtLeastOne);
            return false;
        }
        if (killedPlayers.size() > 0 && healedPlayers.size() > 0 //
                || (killedPlayers.size() != 0 && killedPlayers.size() != 1) //
                || (healedPlayers.size() != 0 && healedPlayers.size() != numberOfHeals)) {
            showErrorToast(context, R.string.night_basis_doctors_error_healXOrKillOne, numberOfHeals.toString());
            return false;
        }

        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        if (killedPlayers.size() == 1) {
            nightActionRepository.kill(killedPlayers.get(0), Role.DOCTOR);
            return true;
        }
        for (Player healedPlayer : healedPlayers) {
            nightActionRepository.heal(healedPlayer);
        }
        return true;
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_doctors);
        }

        if (killedPlayers.size() == 1) {
            return context.getResources().getString(R.string.night_basis_action_kill) + " " + killedPlayers.get(0).getName();
        }

        String instructions = "";
        for (Player healedPlayer : healedPlayers) {
            if (healedPlayer.isMutant() && healedPlayer.host()) {
                instructions += context.getResources().getString(R.string.night_basis_action_heal_host) + " " + healedPlayer.getName();
            } else {
                instructions += context.getResources().getString(R.string.night_basis_action_heal) + " " + healedPlayer.getName();
            }
            instructions += "\n\n";
        }
        return instructions;
    }

    @Override
    public void registerAutoValidatedAction() {
        RepositoryManager.getInstance().nightActionRepository().none(Role.DOCTOR);
    }

    @Override
    public int viewTitleResourceId() {
        return Role.DOCTOR.getLabelResourceId();
    }
}
