package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.util.Log;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Genome;

public class GeneticistStepManager extends StepManager {

    private Genome inspectedPlayerGenome;

    public GeneticistStepManager() {
        super(R.string.night_basis_step_geneticist_headerText);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOne);
            return false;
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        inspectedPlayerGenome = nightActionRepository.testGenomeForGeneticist(selectedPlayers.get(0));
        return true;
    }

    @Override
    public StepManager nextStep() {
        return null;
    }

    @Override
    public String afterStepText(Context context) {
        Player inspectedPlayer = selectedPlayers.get(0);
        if (inspectedPlayerGenome == null) {
            String message = "Trying to display the geneticist inspection result, when it has not been done";
            Log.e(GeneticistStepManager.class.getName(), message);
            throw new RuntimeException(message);
        }
        String genomeLabel = context.getResources().getString(inspectedPlayerGenome.getLabelResourcesId());
        return String.format(context.getResources().getString(R.string.night_basis_information_genomeStatus), inspectedPlayer.getName(), genomeLabel);
    }
}
