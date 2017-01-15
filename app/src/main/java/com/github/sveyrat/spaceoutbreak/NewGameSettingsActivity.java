package com.github.sveyrat.spaceoutbreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundStep;
import com.github.sveyrat.spaceoutbreak.domain.constant.Role;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by romain on 17/09/16.
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    private CheckBox psychologistBox;
    private CheckBox computerScientistBox;
    private CheckBox geneticistBox;
    private CheckBox spyBox;
    private CheckBox hackerBox;
    private CheckBox fanaticBox;
    private CheckBox genotype;
    private CheckBox randomize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);

        setContentView(R.layout.activity_new_game_settings);
        psychologistBox = (CheckBox) findViewById(R.id.checkBox_psychologist);
        computerScientistBox = (CheckBox) findViewById(R.id.checkBox_computer_scientist);
        geneticistBox = (CheckBox) findViewById(R.id.checkBox_geneticist);
        spyBox = (CheckBox) findViewById(R.id.checkBox_spy);
        hackerBox = (CheckBox) findViewById(R.id.checkBox_hacker);
        fanaticBox = (CheckBox) findViewById(R.id.checkBox_fanatic);
        genotype = (CheckBox) findViewById(R.id.checkBox_genotype);
        randomize = (CheckBox) findViewById(R.id.checkBox_randomize);

        if (savedInstanceState != null) {
            psychologistBox.setChecked(savedInstanceState.getBoolean("psychoBoxStatus"));
            computerScientistBox.setChecked(savedInstanceState.getBoolean("computerScientistBoxStatus"));
            geneticistBox.setChecked(savedInstanceState.getBoolean("geneBoxStatus"));
            spyBox.setChecked(savedInstanceState.getBoolean("spyBoxStatus"));
            hackerBox.setChecked(savedInstanceState.getBoolean("hackerBoxStatus"));
            fanaticBox.setChecked(savedInstanceState.getBoolean("fanaticBoxStatus"));
            genotype.setChecked(savedInstanceState.getBoolean("genotypeBoxStatus"));
            randomize.setChecked(savedInstanceState.getBoolean("randomizeStatus"));
        }
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putBoolean("psychoBoxStatus", psychologistBox.isChecked());
        outState.putBoolean("computerScientistBoxStatus", computerScientistBox.isChecked());
        outState.putBoolean("geneBoxStatus", geneticistBox.isChecked());
        outState.putBoolean("spyBoxStatus", spyBox.isChecked());
        outState.putBoolean("hackerBoxStatus", hackerBox.isChecked());
        outState.putBoolean("fanaticBoxStatus", fanaticBox.isChecked());
        outState.putBoolean("genotypeBoxStatus", genotype.isChecked());
        outState.putBoolean("randomizeStatus", randomize.isChecked());
    }

    public void validateGameSettings(View view) {
        List<Role> additionalRoles = new ArrayList<>();
        if (psychologistBox.isChecked()) {
            additionalRoles.add(Role.PSYCHOLOGIST);
        }
        if (computerScientistBox.isChecked()) {
            additionalRoles.add(Role.COMPUTER_SCIENTIST);
        }
        if (geneticistBox.isChecked()) {
            additionalRoles.add(Role.GENETICIST);
        }
        if (spyBox.isChecked()) {
            additionalRoles.add(Role.SPY);
        }
        if (hackerBox.isChecked()) {
            additionalRoles.add(Role.HACKER);
        }
        if (fanaticBox.isChecked()) {
            additionalRoles.add(Role.FANATIC);
        }

        int numberOfPlayers = RepositoryManager.getInstance().gameInformationRepository().countPlayers();
        int numberOfRoles = getResources().getInteger(R.integer.required_roles_number) + additionalRoles.size();
        if (numberOfPlayers < numberOfRoles) {
            Toast toast = Toast.makeText(NewGameSettingsActivity.this, getResources().getString(R.string.new_game_settings_too_many_roles), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }
        RepositoryManager.getInstance().initGameRepository().initializeRoles(additionalRoles, randomize.isChecked(), genotype.isChecked());
        RepositoryManager.getInstance().nightActionRepository().newRound();

        // TODO here there should be a step for the doctors to recognize themself, then the captain election
        RoundStep nextStep = RepositoryManager.getInstance().gameInformationRepository().nextStep();
        Intent nightBasisActivityIntent = new Intent(this, CaptainElectionActivity.class);
        startActivity(nightBasisActivityIntent);
    }
}
