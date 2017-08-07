package com.github.sveyrat.spaceoutbreak.display.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.github.sveyrat.spaceoutbreak.R;
import com.github.sveyrat.spaceoutbreak.dao.RepositoryManager;
import com.github.sveyrat.spaceoutbreak.dao.dto.VoteResult;
import com.github.sveyrat.spaceoutbreak.dao.repository.VoteRepository;
import com.github.sveyrat.spaceoutbreak.display.RoundPhaseToActivityManager;
import com.github.sveyrat.spaceoutbreak.display.adapter.PlayerVoteAdapter;
import com.github.sveyrat.spaceoutbreak.domain.Player;
import com.github.sveyrat.spaceoutbreak.log.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Rom on 05/10/2016.
 */

public class VoteActivity extends AppCompatActivity {

    private List<Player> players;
    private CharSequence[] playersToVote;
    private PlayerVoteAdapter adapter;
    private Map<Player, Player> votes;
    private GridView gridView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);
        players = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();
        playersToVote = putPlayerNamesInCharSequence(players);
        votes = new HashMap<>();
        setContentView(R.layout.activity_vote);


        if (savedInstanceState != null) {
            votes = (Map<Player, Player>) savedInstanceState.getSerializable("votes");
            // adapter.notifyDataSetChanged();
        }
        adapter = new PlayerVoteAdapter(this, players, votes);
        gridView = (GridView) findViewById(R.id.vote_list_players);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, final View selectedView, int position, long id) {
                final Player player = adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
                String message = String.format(getResources().getString(R.string.vote_activity_dialog_title), player.getName());

                int tempVote = 0;
                if (votes.containsKey(player) && votes.get(player) != null) {
                    tempVote = getPlayerPositionInCharSequence(playersToVote, votes.get(player));
                }
                builder.setTitle(message)
                        .setSingleChoiceItems(playersToVote, tempVote, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {

                                // The 'which' argument contains the index position
                                // of the selected item
                                int finalChoicePosition = which - 1;
                                if (votes.containsKey(player)) {
                                    votes.remove(player);
                                }

                                if (which != 0) {
                                    votes.put(player, players.get(finalChoicePosition));
                                } else {
                                    votes.put(player, null);
                                }
                                updateView();

                                // Slight pause before closing AlertDialog
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                }, 500);
                            }

                        });
                builder.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        outState.putSerializable("votes", new HashMap<>(votes));
    }

    public void confirm(View view) {
        if (votes.isEmpty()) {
            Toast toast = Toast.makeText(this, getResources().getString(R.string.vote_activity_error_atLeastOneVote), Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            return;
        }

        final VoteRepository voteRepository = RepositoryManager.getInstance().voteRepository();
        final VoteResult voteResult = voteRepository.vote(votes);

        if (voteResult.draw()) {
            Logger.getInstance().info(getClass(), "Vote result is a draw, asking captain to make a choice...");
            AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
            builder.setCancelable(false);
            String title = String.format(getResources().getString(R.string.vote_activity_tie_title), voteRepository.getCaptain().getName());// CF gdoc on how to get this ID here
            final List<Player> tied = voteResult.getTiedPlayers();
            final int chosenPos[] = new int[1];
            CharSequence[] tied_names = Arrays.copyOfRange(putPlayerNamesInCharSequence(tied), 1, tied.size() + 1);
            builder.setTitle(title)
                    .setSingleChoiceItems(tied_names, -1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            chosenPos[0] = which;
                        }

                    });

            builder.setPositiveButton(R.string.common_validate, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    voteRepository.captainVote(tied.get(chosenPos[0]));
                    displayVoteResults(voteResult);
                }
            });
            builder.setNegativeButton(R.string.common_return, null);
            builder.show();

        } else {
            displayVoteResults(voteResult);
        }


    }

    private void displayVoteResults(VoteResult voteResult) {

        String message = "";
        int numberOfNullVotes = players.size() - votes.size();
        message += getResources().getString(R.string.vote_activity_null_vote) + " : " + numberOfNullVotes + "\n";
        message += getResources().getString(R.string.vote_activity_blank_vote) + " : " + voteResult.getNumberOfBlankVotes() + "\n";
        for (Map.Entry<Player, Integer> resultEntry : voteResult.getResults().entrySet()) {
            message += resultEntry.getKey().getName() + " : " + resultEntry.getValue().toString();
            message += "\n";
        }

        AlertDialog.Builder adb = new AlertDialog.Builder(VoteActivity.this);
        adb.setTitle(getResources().getString(R.string.vote_activity_dialog_result_title));
        adb.setMessage(message);
        adb.setCancelable(false);
        adb.setPositiveButton(getResources().getString(R.string.common_validate), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent nextActivityIntent = RoundPhaseToActivityManager.nextRoundPhaseIntent(VoteActivity.this);
                startActivity(nextActivityIntent);
                return;
            }
        });
        adb.show();
        return;
    }

    private void updateView() {
        adapter.setPlayers(players);
        adapter.setVotes(votes);
        gridView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private CharSequence[] putPlayerNamesInCharSequence(List<Player> players) {
        List<String> playerNames = new ArrayList<String>();
        playerNames.add(getResources().getString(R.string.vote_activity_blank_vote));
        for (Player player : players) {
            playerNames.add(player.getName());
        }
        CharSequence[] playersNamesInCharSequence = playerNames.toArray(new CharSequence[players.size()]);
        return playersNamesInCharSequence;
    }

    private int getPlayerPositionInCharSequence(CharSequence[] names, Player player) {
        int position = 0;
        for (CharSequence s : names) {
            if (s.equals(player.getName())) {
                return position;
            }
            position++;
        }
        return -1;
    }


    @Override
    public void onBackPressed() {
    }
}
