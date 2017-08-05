package com.github.sveyrat.spaceoutbreak.display.nightaction;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;
import com.github.sveyrat.spaceoutbreak.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class HackerNightStepManager extends NightStepManager {

    private int headerTextStringResourceId;

    private List<Role> selectedRoles = new ArrayList<>();

    // Filled once the step has been validated
    private Role inspectedRole;
    private Integer numberOfMutants;
    private Player inspectedPlayer;

    public HackerNightStepManager(boolean fakeStep) {
        super(fakeStep, R.string.night_basis_step_hacker_headerText);
    }

    @Override
    public void select(Context context, final ImageView selectedImageView, final Role role) {
        boolean playerSelected = (View.VISIBLE == selectedImageView.getVisibility());
        if (playerSelected) {
            selectedRoles.remove(role);
            selectedImageView.setVisibility(View.GONE);
            return;
        }
        selectedRoles.add(role);
        selectedImageView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean validateStep(Context context) {
        if (selectedRoles.size() != 1) {
            showErrorToast(context, R.string.night_basis_common_error_selectExactlyOneRole);
            return false;
        }
        inspectedRole = selectedRoles.get(0);
        NightActionRepository nightActionRepository = RepositoryManager.getInstance().nightActionRepository();

        if (inspectedRole == Role.COMPUTER_SCIENTIST) {
            numberOfMutants = nightActionRepository.hackComputerScientist();
        } else if (inspectedRole == Role.PSYCHOLOGIST) {
            inspectedPlayer = nightActionRepository.hackPsychologist();
        } else if (inspectedRole == Role.GENETICIST) {
            inspectedPlayer = nightActionRepository.hackGeneticist();
        }
        return true;
    }

    @Override
    public String afterStepText(Context context) {
        if (fakeStep) {
            return context.getResources().getString(R.string.night_basis_fakeStep_hacker);
        }

        if (numberOfMutants == null && inspectedPlayer == null) {
            return String.format( //
                    context.getResources().getString(R.string.night_basis_information_hacker_roleNotPlayed), //
                    context.getResources().getString(inspectedRole.getLabelResourceId()));
        }
        if (inspectedRole == Role.COMPUTER_SCIENTIST) {
            return context.getResources().getString(R.string.night_basis_information_numberOfMutants) + " " + numberOfMutants;
        } else if (inspectedRole == Role.PSYCHOLOGIST) {
            String mutantOrSane = inspectedPlayer.isMutant() ? context.getResources().getString(R.string.night_basis_information_playerStatus_mutant) : context.getResources().getString(R.string.night_basis_information_playerStatus_sane);
            return String.format( //
                    context.getResources().getString(R.string.night_basis_information_hacker_psychologistResult), //
                    inspectedPlayer.getName(), //
                    mutantOrSane);
        } else if (inspectedRole == Role.GENETICIST) {
            String genomeLabel = context.getResources().getString(inspectedPlayer.getGenome().getLabelResourcesId());
            return String.format( //
                    context.getResources().getString(R.string.night_basis_information_hacker_geneticistResult), //
                    inspectedPlayer.getName(), //
                    genomeLabel);
        }
        Logger.getInstance().error(HackerNightStepManager.class.getName(), "The hacked role can not be recognized");
        return "";
    }

    @Override
    public boolean useRoleSelection() {
        return true;
    }

    @Override
    public void registerAutoValidatedAction() {
        RepositoryManager.getInstance().nightActionRepository().none(Role.HACKER);
    }
}
