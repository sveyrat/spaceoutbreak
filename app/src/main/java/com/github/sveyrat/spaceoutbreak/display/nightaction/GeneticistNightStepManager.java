package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Genome;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.github.sveyrat.spaceoutbreak.log.Logger;

public class GeneticistNightStepManager extends NightStepManager {

    private Genome inspectedPlayerGenome;

    public GeneticistNightStepManager(boolean fakeStep) {
        super(fakeStep, R.string.night_basis_step_geneticist_headerText);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedPlayers.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOnePlayer);
            return false;
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        inspectedPlayerGenome = nightActionRepository.testGenomeForGeneticist(selectedPlayers.get(0));
        return true;
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_geneticist);
        }

        Player inspectedPlayer = selectedPlayers.get(0);
        if (inspectedPlayerGenome == null) {
            String message = "Trying to display the geneticist inspection result, when it has not been done";
            Logger.getInstance().error(getClass(), message);
            throw new RuntimeException(message);
        }
        String genomeLabel = context.getResources().getString(inspectedPlayerGenome.getLabelResourcesId());
        return String.format(context.getResources().getString(R.string.night_basis_information_genomeStatus), inspectedPlayer.getName(), genomeLabel);
    }

    @Override
    public void registerAutoValidatedAction() {
        RepositoryManager.getInstance().nightActionRepository().none(Role.GENETICIST);
    }

    @Override
    public int viewTitleResourceId() {
        return Role.GENETICIST.getLabelResourceId();
    }
}
