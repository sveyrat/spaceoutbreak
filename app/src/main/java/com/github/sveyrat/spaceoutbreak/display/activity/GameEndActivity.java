package com.github.sveyrat.spaceoutbreak.display.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;

/**
 * Created by romain on 22/10/16.
 */

public class GameEndActivity extends AppCompatActivity {

    private TextView resultTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        setContentView(R.layout.activity_game_end);
        resultTextView = (TextView) findViewById(R.id.result_activity_tv);

        int nbMutants = RepositoryManager.getInstance().gameInformationRepository().countMutantsInCurrentGame();
        String winners = getResources().getString(R.string.game_end_activity_mutants_name);
        if (nbMutants == 0) {
            winners = getResources().getString(R.string.game_end_activity_astronauts_name);
        }
        String message = String.format(getResources().getString(R.string.game_end_activity_text), winners);
        resultTextView.setText(message);
    }


    public void newGame(View view) {
        Intent newGameIntent = new Intent(this, NewGameInputPlayerActivity.class);
        startActivity(newGameIntent);
    }

    @Override
    public void onBackPressed() {
    }
}
