package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.SpyInspectionResult;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;

public class SpyStepManager extends StepManager {

    private SpyInspectionResult spyInspectionResult;

    public SpyStepManager() {
        super(R.string.night_basis_step_spy_headerText);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOnePlayer);
            return false;
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        spyInspectionResult = nightActionRepository.inspectAsSpy(selectedPlayers.get(0));
        return true;
    }

    @Override
    public StepManager nextStep() {
        return new HackerStepManager();
    }

    @Override
    public String afterStepText(Context context) {
        Player inspectedPlayer = selectedPlayers.get(0);
        if (spyInspectionResult == null) {
            String message = "Trying to display the spy inspection result, when it has not been done";
            Log.e(SpyStepManager.class.getName(), message);
            throw new RuntimeException(message);
        }
        String inspectionResult = String.format(context.getResources().getString(R.string.night_basis_information_spy_introduction), inspectedPlayer.getName());
        inspectionResult += "\n";
        String yes = context.getResources().getString(R.string.common_yes);
        String no = context.getResources().getString(R.string.common_no);
        inspectionResult += context.getResources().getString(R.string.night_basis_information_spy_mutated) + " " + (spyInspectionResult.isMutated() ? yes : no) + "\n";
        inspectionResult += context.getResources().getString(R.string.night_basis_information_spy_paralyzed) + " " + (spyInspectionResult.isParalyzed() ? yes : no) + "\n";
        inspectionResult += context.getResources().getString(R.string.night_basis_information_spy_healed) + " " + (spyInspectionResult.isHealed() ? yes : no) + "\n";
        inspectionResult += context.getResources().getString(R.string.night_basis_information_spy_inspectedByPsychologist) + " " + (spyInspectionResult.isInspectedByPsychologist() ? yes : no) + "\n";
        inspectionResult += context.getResources().getString(R.string.night_basis_information_spy_inspectedByGeneticist) + " " + (spyInspectionResult.isInspectedByGeneticist() ? yes : no) + "\n";
        return inspectionResult;
    }
}
