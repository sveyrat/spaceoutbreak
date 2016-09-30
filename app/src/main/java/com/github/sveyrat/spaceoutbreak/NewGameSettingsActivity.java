package com.github.sveyrat.spaceoutbreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
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
    }
}
