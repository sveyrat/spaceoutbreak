package com.github.sveyrat.spaceoutbreak.display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundPhase;
import com.github.sveyrat.spaceoutbreak.display.RoundPhaseToActivityManager;
import com.github.sveyrat.spaceoutbreak.display.adapter.PlayerNightAdapter;
import com.github.sveyrat.spaceoutbreak.display.adapter.RoleNightAdapter;
import com.github.sveyrat.spaceoutbreak.display.nightaction.NightStepManager;
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

    private NightStepManager nightStepManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        nightStepManager = RepositoryManager.getInstance().nightActionRepository().nextNightStep();

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
                nightStepManager.select(NightBasisActivity.this, selectedImageView, player);
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
                nightStepManager.select(NightBasisActivity.this, selectedImageView, role);
            }
        });

        updateView();
    }


    public void confirm(View view) {
        if (View.GONE == afterStepTextView.getVisibility()) {
            boolean validationResult = nightStepManager.validateStep(this);
            if (!validationResult) {
                return;
            }
        }

        String afterStepText = nightStepManager.afterStepText(this);
        if (View.GONE == afterStepTextView.getVisibility() && afterStepText != null) {
            showAfterStepText(afterStepText);
            return;
        } else {
            nightStepManager = RepositoryManager.getInstance().nightActionRepository().nextNightStep();

            if (nightStepManager == null) {
                RoundPhase nextRoundPhase = RepositoryManager.getInstance().gameInformationRepository().nextRoundStep();
                Intent nextActivityIntent = RoundPhaseToActivityManager.goToActivityIntent(this, nextRoundPhase);
                startActivity(nextActivityIntent);
                return;
            }

            updateView();
            if (nightStepManager.useRoleSelection()) {
                showRoleGrid();
            }
            if (nightStepManager.autoValidate()) {
                showAfterStepText(afterStepText);
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

        headerTextView.setText(nightStepManager.headerText(this));
    }

    @Override
    public void onBackPressed() {
    }
}
