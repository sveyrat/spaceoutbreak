package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;

public class PsychologistStepManager extends StepManager {

    private Boolean inspectedPlayerIsMutant;

    public PsychologistStepManager() {
        super(R.string.night_basis_step_psychologist_headerText);
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
    public StepManager nextStep() {
        return new GeneticistStepManager();
    }

    @Override
    public String afterStepText(Context context) {
        Player inspectedPlayer = selectedPlayers.get(0);
        if (inspectedPlayerIsMutant == null) {
            String message = "Trying to display the psychologist inspection result, when it has not been done";
            Log.e(PsychologistStepManager.class.getName(), message);
            throw new RuntimeException(message);
        }
        String mutantOrSane = inspectedPlayerIsMutant ? context.getResources().getString(R.string.night_basis_information_playerStatus_mutant) : context.getResources().getString(R.string.night_basis_information_playerStatus_sane);
        return String.format(context.getResources().getString(R.string.night_basis_information_playerStatus), inspectedPlayer.getName(), mutantOrSane);
    }
}
