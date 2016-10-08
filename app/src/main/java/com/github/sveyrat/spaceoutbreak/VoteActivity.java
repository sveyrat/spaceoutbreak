package com.github.sveyrat.spaceoutbreak;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.display.PlayerVoteAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;

import java.util.List;

/**
 * Created by Rom on 05/10/2016.
 */

public class VoteActivity extends AppCompatActivity {
    private List<Player> players;
    private PlayerVoteAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();

        setContentView(R.layout.activity_vote);

        adapter = new PlayerVoteAdapter(this, players);
        GridView gridView = (GridView) findViewById(R.id.vote_list_players);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                //final String itemName = ((TextView) v).getText().toString();
                final Player player = adapter.getItem(position);
            }

        });

    }


    void updateView() {
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        adapter.notifyDataSetChanged();

    }
}

