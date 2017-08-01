package com.github.sveyrat.spaceoutbreak;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PreviousGamesAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Game;

import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private ListView gamesList;
    private PreviousGamesAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        setContentView(R.layout.activity_home);
        List<Game> previousGames = RepositoryManager.getInstance().gameInformationRepository().lastTwoGames();
        gamesAdapter = new PreviousGamesAdapter(this, previousGames);

        gamesList = (ListView) findViewById(R.id.home_previous_game_list);
        gamesList.setAdapter(gamesAdapter);

        gamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                Game game = gamesAdapter.getItem(position);
                // TODO launch the right activity here based on the game's next step
            }
        });


    }

    public void newGame(View view) {
        Intent newGameIntent = new Intent(this, NewGameInputPlayerActivity.class);
        startActivity(newGameIntent);
    }
}
