package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.ArrayList;
import java.util.List;

public class PsychologistStepManager extends StepManager {

    private List<Player> selectedPlayers = new ArrayList<>();
    private Boolean inspectedPlayerIsMutant;

    public PsychologistStepManager() {
        super(R.string.night_basis_step_psychologist_headerText);
    }

    @Override
    public void select(Context context, ImageView selectedImageView, Player player) {
        boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            selectedPlayers.remove(player);
            selectedImageView.setVisibility(View.GONE);
            return;
        }
        selectedPlayers.add(player);
        selectedImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOne);
            return false;
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        inspectedPlayerIsMutant = nightActionRepository.testIfMutantForPsychologist(selectedPlayers.get(0));
        return true;
    }

    @Override
    public StepManager nextStep() {
        return null;
    }

    @Override
    public String afterStepText(Context context) {
        Player inspectedPlayer = selectedPlayers.get(0);
        if (inspectedPlayerIsMutant == null) {
            String message = "Trying to display the psychologist inspection result, when it has not been done";
            Log.e(PsychologistStepManager.class.getName(), message);
            throw  new RuntimeException(message);
        }
        String mutantOrSane = inspectedPlayerIsMutant ? context.getResources().getString(R.string.night_basis_information_playerStatus_mutant) : context.getResources().getString(R.string.night_basis_information_playerStatus_sane);
        return String.format(context.getResources().getString(R.string.night_basis_information_playerStatus), inspectedPlayer.getName(), mutantOrSane);
    }
}
