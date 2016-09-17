package com.github.sveyrat.spaceoutbreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;

/**
 * Created by romain on 17/09/16.
 */
public class NewGameSettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game_settings);
    }

    public void validatePlayerList(View view) {
        // TODO
    }
}
