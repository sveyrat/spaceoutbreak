package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

public class ComputerScientistStepManager extends StepManager {

    public ComputerScientistStepManager(boolean fakeStep) {
        super(fakeStep, R.string.night_basis_step_healOrKill_headerText);
    }

    @Override
    public void select(Context context, ImageView selectedImageView, Player player) {
        // do nothing : no player selection in this step
    }

    @Override
    public boolean validateStep(Context context) {
        String message = "The computer scientist step validation should never be called.";
        Log.e(ComputerScientistStepManager.class.getName(), message);
        throw new RuntimeException(message);
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_computerScientist);
        }
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();
        int numberOfMutants = nightActionRepository.countMutantsForComputerScientist();
        return context.getResources().getString(R.string.night_basis_information_numberOfMutants) + " " + numberOfMutants;
    }

    @Override
    public boolean autoValidate() {
        return true;
    }

    @Override
    public Role currentlyPlayedRole() {
        return Role.COMPUTER_SCIENTIST;
    }
}
