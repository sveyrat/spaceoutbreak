package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.github.sveyrat.spaceoutbreak.log.Logger;

public class PsychologistNightStepManager extends NightStepManager {

    private Boolean inspectedPlayerIsMutant;

    public PsychologistNightStepManager(boolean fakeStep) {
        super(fakeStep, R.string.night_basis_step_psychologist_headerText);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOnePlayer);
            return false;
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        inspectedPlayerIsMutant = nightActionRepository.testIfMutantForPsychologist(selectedPlayers.get(0));
        return true;
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_psychologist);
        }

        Player inspectedPlayer = selectedPlayers.get(0);
        if (inspectedPlayerIsMutant == null) {
            String message = "Trying to display the psychologist inspection result, when it has not been done";
            Logger.getInstance().error(PsychologistNightStepManager.class.getName(), message);
            throw new RuntimeException(message);
        }
        String mutantOrSane = inspectedPlayerIsMutant ? context.getResources().getString(R.string.night_basis_information_playerStatus_mutant) : context.getResources().getString(R.string.night_basis_information_playerStatus_sane);
        return String.format(context.getResources().getString(R.string.night_basis_information_playerStatus), inspectedPlayer.getName(), mutantOrSane);
    }

    @Override
    public void registerAutoValidatedAction() {
        RepositoryManager.getInstance().nightActionRepository().none(Role.PSYCHOLOGIST);
    }
}
