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

    private List<Player> votingPlayers;
    private List<Player> voteChoices;
    private CharSequence[] voteChoicesAsCharSequence;
    private PlayerVoteAdapter votingPlayersGridAdapter;
    private Map<Player, Player> votes;
    private GridView votingPlayersGrid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RepositoryManager.init(this);

        votingPlayers = RepositoryManager.getInstance().gameInformationRepository().loadAlivePlayers();

        voteChoices = new ArrayList<>();
        // Blank vote
        voteChoices.add(null);
        for (Player votingPlayer : votingPlayers) {
            voteChoices.add(votingPlayer);
        }
        voteChoicesAsCharSequence = putPlayerNamesInCharSequence(voteChoices);

        votes = new HashMap<>();
        setContentView(R.layout.activity_vote);

        if (savedInstanceState != null) {
            votes = (Map<Player, Player>) savedInstanceState.getSerializable("votes");
            // votingPlayersGridAdapter.notifyDataSetChanged();
        }
        votingPlayersGridAdapter = new PlayerVoteAdapter(this, votingPlayers, votes);
        votingPlayersGrid = (GridView) findViewById(R.id.vote_list_players);
        votingPlayersGrid.setAdapter(votingPlayersGridAdapter);


        votingPlayersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, final View selectedView, int position, long id) {
                final Player votingPlayer = votingPlayersGridAdapter.getItem(position);

                AlertDialog.Builder voteForAlertBuilder = new AlertDialog.Builder(VoteActivity.this);
                String message = String.format(getResources().getString(R.string.vote_activity_dialog_title), votingPlayer.getName());

                int previousVoteIndex = -1;
                if (votes.containsKey(votingPlayer)) {
                    previousVoteIndex = getPlayerPositionInCharSequence(voteChoicesAsCharSequence, votes.get(votingPlayer));
                }
                voteForAlertBuilder.setTitle(message)
                        .setSingleChoiceItems(voteChoicesAsCharSequence, previousVoteIndex, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog, int which) {
                                if (votes.containsKey(votingPlayer)) {
                                    votes.remove(votingPlayer);
                                }

                                votes.put(votingPlayer, voteChoices.get(which));
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
                voteForAlertBuilder.show();
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
        final VoteResult voteResult;
        if (!RepositoryManager.getInstance().gameInformationRepository().votingPhaseStarted()) {
            voteResult = voteRepository.vote(votes);
        } else {
            voteResult = voteRepository.voteResult();
        }

        if (voteResult.draw()) {
            Logger.getInstance().info(getClass(), "Vote result is a draw, asking captain to make a choice...");
            AlertDialog.Builder builder = new AlertDialog.Builder(VoteActivity.this);
            builder.setCancelable(false);
            String title = String.format(getResources().getString(R.string.vote_activity_tie_title), voteRepository.getCaptain().getName());// CF gdoc on how to get this ID here
            final List<Player> tied = voteResult.getTiedPlayers();
            final int chosenPos[] = new int[1];
            CharSequence[] tied_names = putPlayerNamesInCharSequence(tied);
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
        int numberOfNullVotes = votingPlayers.size() - votes.size();
        message += getResources().getString(R.string.vote_activity_null_vote) + " : " + numberOfNullVotes + "\n";
        for (Map.Entry<Player, Integer> resultEntry : voteResult.getResults().entrySet()) {
            String playerName;
            if (resultEntry.getKey() == null) {
                playerName = blankVoteLabel();
            } else {
                playerName = resultEntry.getKey().getName();
            }
            message += playerName + " : " + resultEntry.getValue().toString();
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
    }

    private void updateView() {
        votingPlayersGridAdapter.setPlayers(votingPlayers);
        votingPlayersGridAdapter.setVotes(votes);
        votingPlayersGrid.setAdapter(votingPlayersGridAdapter);
        votingPlayersGridAdapter.notifyDataSetChanged();
    }

    private CharSequence[] putPlayerNamesInCharSequence(List<Player> players) {
        List<String> playerNames = new ArrayList<>();
        for (Player player : players) {
            if (player == null) {
                playerNames.add(blankVoteLabel());
                continue;
            }
            playerNames.add(player.getName());
        }
        CharSequence[] playersNamesInCharSequence = playerNames.toArray(new CharSequence[players.size()]);
        return playersNamesInCharSequence;
    }

    private int getPlayerPositionInCharSequence(CharSequence[] names, Player player) {
        int position = 0;
        for (CharSequence s : names) {
            if ((player == null && s.equals(blankVoteLabel())) || s.equals(player.getName())) {
                return position;
            }
            position++;
        }
        return -1;
    }

    private String blankVoteLabel() {
        return getResources().getString(R.string.vote_activity_blank_vote);
    }


    @Override
    public void onBackPressed() {
    }
}

