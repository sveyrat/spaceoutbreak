package com.github.sveyrat.spaceoutbreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundStep;
import com.github.sveyrat.spaceoutbreak.dao.repository.NightActionRepository;
import com.github.sveyrat.spaceoutbreak.display.PlayerNightAdapter;

import android.widget.ImageView;

import com.github.sveyrat.spaceoutbreak.display.RoleNightAdapter;
import com.github.sveyrat.spaceoutbreak.display.nightaction.MutantsMutateOrKillStepManager;
import com.github.sveyrat.spaceoutbreak.display.nightaction.StepManager;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {

    private TextView headerTextView;
    private TextView mutantCounter;
    private TextView afterStepTextView;
    private GridView playerGrid;
    private GridView roleGrid;
    private PlayerNightAdapter playerAdapter;
    private RoleNightAdapter roleAdapter;

    private StepManager stepManager = new MutantsMutateOrKillStepManager();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        setContentView(R.layout.activity_night_basis);

        headerTextView = (TextView) findViewById(R.id.night_basis_step_tv);
        mutantCounter = (TextView) findViewById(R.id.mutant_counter);
        afterStepTextView = (TextView) findViewById(R.id.after_step_text);

        List<Player> alivePlayers = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        playerAdapter = new PlayerNightAdapter(this, alivePlayers);
        playerGrid = (GridView) findViewById(R.id.night_basis_list_players);
        playerGrid.setAdapter(playerAdapter);

        playerGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Player player = playerAdapter.getItem(position);
                ImageView selectedImageView = (ImageView) v.findViewById(R.id.selected_image);
                stepManager.select(NightBasisActivity.this, selectedImageView, player);
            }
        });

        List<Role> hackableRoles = new ArrayList<>();
        hackableRoles.add(Role.COMPUTER_SCIENTIST);
        hackableRoles.add(Role.PSYCHOLOGIST);
        hackableRoles.add(Role.GENETICIST);
        roleAdapter = new RoleNightAdapter(this, hackableRoles);
        roleGrid = (GridView) findViewById(R.id.role_list);
        roleGrid.setAdapter(roleAdapter);

        roleGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Role role = roleAdapter.getItem(position);
                ImageView selectedImageView = (ImageView) v.findViewById(R.id.selected_image);
                stepManager.select(NightBasisActivity.this, selectedImageView, role);
            }
        });

        updateView();
    }


    public void confirm(View view) {
        if (View.GONE == afterStepTextView.getVisibility()) {
            boolean validationResult = stepManager.validateStep(this);
            if (!validationResult) {
                return;
            }
        }

        String afterStepText = stepManager.afterStepText(this);
        if (View.GONE == afterStepTextView.getVisibility() && afterStepText != null) {
            showAfterStepText(afterStepText);
            return;
        } else {
            stepManager = RepositoryManager.getInstance().nightActionRepository().nextStep(stepManager.currentlyPlayedRole());

            if (stepManager == null) {
                RoundStep nextStep = RepositoryManager.getInstance().gameInformationRepository().nextStep();
                if (RoundStep.END == nextStep) {
                    startActivity(new Intent(this, GameEndActivity.class));
                    return;
                }
                if (RoundStep.CAPTAIN_ELECTION == nextStep) {
                    startActivity(new Intent(this, CaptainElectionActivity.class));
                    return;
                }
                if (RoundStep.DAY == nextStep) {
                    startActivity(new Intent(this, VoteActivity.class));
                    return;
                }
                String message = "Game next step is inconsistent with current status. Next step is " + nextStep.toString();
                Log.e(NightBasisActivity.class.getName(), message);
                throw new RuntimeException(message);
            }

            updateView();
            if (stepManager.useRoleSelection()) {
                showRoleGrid();
            }
            if (stepManager.autoValidate()) {
                showAfterStepText(stepManager.afterStepText(this));
            }
        }
    }

    private void showAfterStepText(String afterStepText) {
        headerTextView.setText(getResources().getString(R.string.night_basis_gameMasterAction_headerText));
        afterStepTextView.setText(afterStepText);
        afterStepTextView.setVisibility(View.VISIBLE);
        playerGrid.setVisibility(View.GONE);
        roleGrid.setVisibility(View.GONE);
    }

    private void showRoleGrid() {
        afterStepTextView.setVisibility(View.GONE);
        playerGrid.setVisibility(View.GONE);
        roleGrid.setVisibility(View.VISIBLE);
    }

    private void updateView() {
        afterStepTextView.setText("");
        afterStepTextView.setVisibility(View.GONE);
        playerGrid.setVisibility(View.VISIBLE);
        roleGrid.setVisibility(View.GONE);

        List<Player> alivePlayers = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        playerAdapter.setPlayers(alivePlayers);
        playerAdapter.notifyDataSetChanged();

        Integer nbMutants = RepositoryManager.getInstance().gameInformationRepository().countMutantsInCurrentGame();
        mutantCounter.setText(nbMutants.toString());

        headerTextView.setText(stepManager.headerText(this));
    }

    @Override
    public void onBackPressed() {
    }
}
