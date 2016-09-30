package com.github.sveyrat.spaceoutbreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;

/**
 * Created by Rom on 22/09/2016.
 */
public class NightBasisActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);

        setContentView(R.layout.activity_new_game_input_player);
    }
}
