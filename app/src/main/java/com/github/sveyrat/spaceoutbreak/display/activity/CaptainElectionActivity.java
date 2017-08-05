package com.github.sveyrat.spaceoutbreak.display.activity;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.RoundPhase;
import com.github.sveyrat.spaceoutbreak.display.RoundPhaseToActivityManager;
import com.github.sveyrat.spaceoutbreak.display.adapter.CaptainVoteAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;


import java.util.List;


/**
 * Created by Rom on 15/01/2017.
 */

public class CaptainElectionActivity extends AppCompatActivity {

    private List<Player> players;
    private CaptainVoteAdapter adapter;
    private GridView gridView;
    private Player voted;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        setContentView(R.layout.activity_vote);


        if (savedInstanceState != null) {
            voted = (Player) savedInstanceState.getSerializable("voted");
        }
        adapter = new CaptainVoteAdapter(this, players, voted);
        gridView = (GridView) findViewById(R.id.vote_list_players);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, final View selectedView, int position, long id) {
                final Player player = adapter.getItem(position);

                if (voted == null) {
                    voted = player;
                } else {
                    if (voted.equals(player)) {
                        voted = null;
                    } else {
                        voted = player;
                    }
                }
                updateView();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("voted", voted);
    }

    public void confirm(View view) {
        if (voted == null) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.captain_election_error_missVote), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(CaptainElectionActivity.this);
            String message = String.format(getResources().getString(R.string.captain_election_validate), voted.getName());
            adb.setMessage(message);
            adb.setNegativeButton(getResources().getString(R.string.new_game_player_input_no), null);
            adb.setPositiveButton(getResources().getString(R.string.new_game_player_input_yes), new AlertDialog.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    RepositoryManager.getInstance().voteRepository().defineCaptain(voted);
                    RoundPhase nextRoundPhase = RepositoryManager.getInstance().gameInformationRepository().nextRoundStep();
                    Intent nextActivityIntent = RoundPhaseToActivityManager.goToActivityIntent(CaptainElectionActivity.this, nextRoundPhase);
                    startActivity(nextActivityIntent);
                    return;
                }
            });
            adb.show();
        }

    }

    private void updateView() {
        adapter.setPlayers(players);
        adapter.setVoted(voted);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
    }

}
