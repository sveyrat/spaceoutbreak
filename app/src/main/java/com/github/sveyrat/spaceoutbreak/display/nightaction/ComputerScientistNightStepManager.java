package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.github.sveyrat.spaceoutbreak.log.Logger;

public class ComputerScientistNightStepManager extends NightStepManager {

    public ComputerScientistNightStepManager(boolean fakeStep) {
        super(fakeStep, R.string.night_basis_step_computerScientist_headerText);
    }

    @Override
    public void select(Context context, ImageView selectedImageView, Player player) {
        // do nothing : no player selection in this step
    }

    @Override
    public boolean validateStep(Context context) {
        // Auto validated step : we should never get here
        String message = "The computer scientist step validation should never be called.";
        Logger.getInstance().error(getClass(), message);
        throw new RuntimeException(message);
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_computerScientist);
        }
        int numberOfMutants = RepositoryManager.getInstance().nightActionRepository().countMutantsForComputerScientist();
        return context.getResources().getString(R.string.night_basis_information_numberOfMutants) + " " + numberOfMutants;
    }

    @Override
    public void registerAutoValidatedAction() {
        // This step is always auto-validated (no player action required),
        // so we need to register a "none" action only if the step has been faked
        if (fakeStep) {
            RepositoryManager.getInstance().nightActionRepository().none(Role.COMPUTER_SCIENTIST);
        }
    }

    @Override
    public boolean autoValidate() {
        return true;
    }
}
