package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.NewGameSettingsActivity;
import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.ArrayList;
import java.util.List;

public class MutantsMutateOrKillStepManager extends StepManager {

    private List<Player> mutedPlayers = new ArrayList<>();
    private List<Player> killedPlayers = new ArrayList<>();

    @Override
    public String headerText(Context context) {
        return context.getResources().getString(R.string.night_basis_step_mutant_headerText);
    }

    @Override
    public void select(Context context, final ImageView selectedImageView, final Player player) {
        final boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            mutedPlayers.remove(player);
            killedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        String alertMessage = String.format(context.getResources().getString(R.string.common_player_name), player.getName());
        alertDialogBuilder.setMessage(alertMessage);
        alertDialogBuilder.setNegativeButton(context.getResources().getString(R.string.night_basis_action_mutate), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mutedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.setPositiveButton(context.getResources().getString(R.string.night_basis_action_kill), new AlertDialog.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                killedPlayers.add(player);
                selectedImageView.setVisibility(View.VISIBLE);
            }
        });
        alertDialogBuilder.show();
    }

    @Override
    public boolean validateStep(Context context) {
        int numberOfSelectedPlayers = killedPlayers.size() + mutedPlayers.size();
        if (numberOfSelectedPlayers == 0) {
            showErrorToast(context, R.string.night_basis_common_error_selectAtLeastOne);
            return false;
        }
        if (numberOfSelectedPlayers > 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectAtMostOne);
            return false;
        }

        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        if (killedPlayers.size() > 0) {
            nightActionRepository.kill(killedPlayers.get(0), Role.BASE_MUTANT);
            return true;
        }
        nightActionRepository.mutate(mutedPlayers.get(0));
        return true;
    }

    @Override
    public StepManager nextStep() {
        return new MutantsParalyseOrInfectStepManager();
    }
}
