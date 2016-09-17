package com.github.sveyrat.spaceoutbreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.CheckBox;

import com.github.sveyrat.spaceoutbreak.dao.InitGameRepository;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.domain.Role;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by romain on 17/09/16.
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    CheckBox psychologistBox, computerScientistBox, geneticistBox, spyBox, hackerBox, fanaticBox, genotype, randomize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // TODO
        List<Role> additionalRoles = new ArrayList<>();
        if(psychologistBox.isChecked()){additionalRoles.add(Role.PSYCHOLOGIST);}
        if(computerScientistBox.isChecked()){additionalRoles.add(Role.COMPUTER_SCIENTIST);}
        if(geneticistBox.isChecked()){additionalRoles.add(Role.GENETICIST);}
        if(spyBox.isChecked()){additionalRoles.add(Role.SPY);}
        if(hackerBox.isChecked()){additionalRoles.add(Role.HACKER);}
        if(fanaticBox.isChecked()){additionalRoles.add(Role.FANATIC);}
        InitGameRepository.initializeRoles(gameId, additionalRoles, genotype.isChecked(), randomize.isChecked());

    }



}
